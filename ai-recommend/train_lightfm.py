# train_lightfm.py (FIXED: feature mapping error 해결)
# --------------------------------------------------
# 핵심 수정 사항:
# 1) Dataset.fit() 단계에서 user_features / item_features를 사전 등록
# 2) interaction에 포함된 trip_id를 OPEN 상태 trip으로 필터링
# --------------------------------------------------

import os
from datetime import datetime

import numpy as np
import pandas as pd
from tqdm import tqdm
from dotenv import load_dotenv

from lightfm import LightFM
from lightfm.data import Dataset

from db import load_df, execute, execute_many

load_dotenv()

# =====================
# 환경 변수
# =====================
MODEL_VERSION = os.getenv("MODEL_VERSION", "v2_lightfm")
TOP_N = int(os.getenv("TOP_N", "20"))
EPOCHS = int(os.getenv("EPOCHS", "20"))
NO_COMPONENTS = int(os.getenv("NO_COMPONENTS", "32"))
LEARNING_RATE = float(os.getenv("LEARNING_RATE", "0.05"))


# =====================
# 1) 데이터 로드
# =====================

def load_data():
    users = load_df("""
        SELECT user_id, region
        FROM user
    """)

    trips = load_df("""
        SELECT trip_id, region, theme
        FROM trip
        WHERE status = 'OPEN'
    """)

    likes = load_df("""
        SELECT l.user_id, l.trip_id, 3 AS weight
        FROM trip_like l
        JOIN trip t ON l.trip_id = t.trip_id
        WHERE t.status = 'OPEN'
    """)

    applies = load_df("""
        SELECT a.user_id, a.trip_id, 5 AS weight
        FROM application a
        JOIN trip t ON a.trip_id = t.trip_id
        WHERE a.status = 'APPROVED'
          AND t.status = 'OPEN'
    """)

    interactions = pd.concat([likes, applies], ignore_index=True)
    interactions = interactions.groupby(["user_id", "trip_id"], as_index=False)["weight"].sum()

    user_lang = load_df("SELECT user_id, language_code FROM user_language")
    trip_lang = load_df("SELECT trip_id, language_code FROM trip_language")

    return users, trips, interactions, user_lang, trip_lang


# =====================
# 2) LightFM Matrix 생성
# =====================

def build_lightfm_matrices(users, trips, interactions, user_lang, trip_lang):
    dataset = Dataset()

    # ---- feature key 사전 수집  ----
    user_feature_keys = set()
    item_feature_keys = set()

    for _, r in users.iterrows():
        if pd.notna(r.region):
            user_feature_keys.add(f"region:{str(r.region).upper()}")

    for _, r in user_lang.iterrows():
        if pd.notna(r.language_code):
            user_feature_keys.add(f"lang:{str(r.language_code).upper()}")

    for _, r in trips.iterrows():
        if pd.notna(r.region):
            item_feature_keys.add(f"region:{str(r.region).upper()}")
        if pd.notna(r.theme):
            item_feature_keys.add(f"theme:{str(r.theme).upper()}")

    for _, r in trip_lang.iterrows():
        if pd.notna(r.language_code):
            item_feature_keys.add(f"lang:{str(r.language_code).upper()}")

    # ---- Dataset.fit (🔥 feature 등록 필수) ----
    dataset.fit(
        users=users.user_id.tolist(),
        items=trips.trip_id.tolist(),
        user_features=list(user_feature_keys),
        item_features=list(item_feature_keys)
    )

    # ---- interaction matrix ----
    valid_trip_ids = set(trips.trip_id)
    interactions = interactions[interactions.trip_id.isin(valid_trip_ids)]

    (interaction_matrix, weight_matrix) = dataset.build_interactions(
        [(r.user_id, r.trip_id, r.weight) for r in interactions.itertuples(index=False)]
    )

    # ---- item feature matrix ----
    trip_lang_map = trip_lang.groupby("trip_id")["language_code"].apply(list).to_dict()
    item_features = []

    for r in trips.itertuples(index=False):
        feats = []
        if pd.notna(r.region):
            feats.append(f"region:{str(r.region).upper()}")
        if pd.notna(r.theme):
            feats.append(f"theme:{str(r.theme).upper()}")
        for lang in trip_lang_map.get(r.trip_id, []):
            feats.append(f"lang:{str(lang).upper()}")
        item_features.append((r.trip_id, feats))

    item_features_matrix = dataset.build_item_features(item_features)

    # ---- user feature matrix ----
    user_lang_map = user_lang.groupby("user_id")["language_code"].apply(list).to_dict()
    user_features = []

    for r in users.itertuples(index=False):
        feats = []
        if pd.notna(r.region):
            feats.append(f"region:{str(r.region).upper()}")
        for lang in user_lang_map.get(r.user_id, []):
            feats.append(f"lang:{str(lang).upper()}")
        user_features.append((r.user_id, feats))

    user_features_matrix = dataset.build_user_features(user_features)

    return dataset, interaction_matrix, weight_matrix, user_features_matrix, item_features_matrix


# =====================
# 3) 모델 학습
# =====================

def train_model(interaction_matrix, weight_matrix, user_features, item_features):
    model = LightFM(
        loss="warp",
        no_components=NO_COMPONENTS,
        learning_rate=LEARNING_RATE
    )

    model.fit(
        interaction_matrix,
        sample_weight=weight_matrix,
        user_features=user_features,
        item_features=item_features,
        epochs=EPOCHS,
        num_threads=1
    )

    return model


# =====================
# 4) 추천 생성
# =====================

def generate_recommendations(model, dataset, users, user_features, item_features):
    user_map, _, item_map, _ = dataset.mapping()
    inv_item_map = {v: k for k, v in item_map.items()}

    results = []
    all_items = np.arange(len(item_map))

    for user_id in users.user_id:
        if user_id not in user_map:
            continue

        scores = model.predict(
            user_map[user_id],
            all_items,
            user_features=user_features,
            item_features=item_features
        )

        top_items = np.argsort(-scores)[:TOP_N]
        for idx in top_items:
            results.append((user_id, inv_item_map[idx], float(scores[idx])))

    return results


# =====================
# 5) DB 저장
# =====================

def save_results(results):
    execute("DELETE FROM recommendation_result")

    now = datetime.now()

    sql = """
        INSERT INTO recommendation_result
        (user_id, trip_id, score, model_type, model_version, generated_at)
        VALUES (:user_id, :trip_id, :score, :model_type, :model_version, :generated_at)
    """

    rows = [
        (u, t, s, "LIGHTFM", MODEL_VERSION, now)
        for (u, t, s) in results
    ]

    execute_many(sql, rows)


# =====================
# main
# =====================

def main():
    print("1) Loading data from DB...")
    users, trips, interactions, user_lang, trip_lang = load_data()
    print(f"Users: {len(users)}, Trips: {len(trips)}, Interactions: {len(interactions)}")

    print("2) Building LightFM matrices...")
    dataset, inter_mat, weight_mat, user_feat, item_feat = build_lightfm_matrices(
        users, trips, interactions, user_lang, trip_lang
    )

    print("3) Training model...")
    model = train_model(inter_mat, weight_mat, user_feat, item_feat)

    print("4) Generating recommendations...")
    results = generate_recommendations(model, dataset, users, user_feat, item_feat)

    print("5) Saving to DB...")
    save_results(results)

    print("✅ Done")


if __name__ == "__main__":
    main()

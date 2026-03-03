import os
import pandas as pd
from sqlalchemy import create_engine, text
from dotenv import load_dotenv
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent
load_dotenv(BASE_DIR / ".env")

def make_engine():
    host = os.getenv("DB_HOST")
    port = os.getenv("DB_PORT", "3306")
    db = os.getenv("DB_NAME")
    user = os.getenv("DB_USER")
    pw = os.getenv("DB_PASSWORD")

    print("[DB CHECK]", host, port, db, user, "PW_SET=" + str(bool(pw)))

    url = f"mysql+pymysql://{user}:{pw}@{host}:{port}/{db}?charset=utf8mb4"
    return create_engine(url)

def load_df(sql):
    engine = make_engine()
    with engine.connect() as conn:
        return pd.read_sql(text(sql), conn)

def execute(sql):
    engine = make_engine()
    with engine.begin() as conn:
        conn.execute(text(sql))

def execute_many(sql, rows):
    engine = make_engine()
    with engine.begin() as conn:
        conn.execute(
            text(sql),
            [
                {
                    "user_id": r[0],
                    "trip_id": r[1],
                    "score": r[2],
                    "model_type": r[3],
                    "model_version": r[4],
                    "generated_at": r[5],
                }
                for r in rows
            ]
        )
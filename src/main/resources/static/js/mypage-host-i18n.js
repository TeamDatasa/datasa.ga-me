console.log("[host-i18n] loaded");

const LANG_KEY = "uiLang";

const HOST_T = {
  ko: {
    back: "戻る",
    tourCreate: "ツアー登録",
    tourCreateMeta: "新しいツアーを登録します",
    tourCreateHint: "ツアーを作成して公開できます。",
    tourManage: "ツアー管理",
    tourManageMeta: "修正 / 削除",
    tourManageHint: "自分が登録したツアーを管理できます。",
    myTours: "登録したツアー",
    myToursHint: "自分が作成したツアーの日程と申請者を管理できます。",
    applicants: "申請者確認",
    emptyTours: "まだ登録したツアーがありません。",
    reviews: "レビュー",
    reviewsHint: "ツアー終了後に作成されたレビューが表示されます。",
    emptyReviews: "まだレビューがありません。",

    reviewTripLabel: "ツアー",
    reviewDateLabel: "作成일",
    reviewRatingLabel: "評価"
  },
  ja: {
    back: "戻る",
    tourCreate: "ツアー登録",
    tourCreateMeta: "新しいツアーを登録します",
    tourCreateHint: "ツアーを作成して公開できます。",
    tourManage: "ツアー管理",
    tourManageMeta: "編集 / 削除",
    tourManageHint: "自分が投稿したツアーを管理できます。",
    myTours: "登録したツアー",
    myToursHint: "作成したツアーの日程と申請者を管理できます。",
    applicants: "申請者確認",
    emptyTours: "まだ登録したツアーがありません。",
    reviews: "レビュー",
    reviewsHint: "ツアー終了後に作成されたレビューが表示されます。",
    emptyReviews: "まだレビューがありません。",

    reviewTripLabel: "ツアー",
    reviewDateLabel: "作成日",
    reviewRatingLabel: "評価"
  },
  en: {
    back: "Back",
    tourCreate: "Create Tour",
    tourCreateMeta: "Register a new tour",
    tourCreateHint: "You can write and publish your tour.",
    tourManage: "Manage Tours",
    tourManageMeta: "Edit / Delete",
    tourManageHint: "Manage the tours you posted.",
    myTours: "My Tours",
    myToursHint: "Manage schedules and applicants for your tours.",
    applicants: "View applicants",
    emptyTours: "No tours yet.",
    reviews: "Reviews",
    reviewsHint: "Reviews written after tours end will appear here.",
    emptyReviews: "No reviews yet.",

    reviewTripLabel: "Trip",
    reviewDateLabel: "Date",
    reviewRatingLabel: "Rating"
  }
};

function t(lang, key){
  const dict = HOST_T[lang] || HOST_T.ko;
  return dict[key] ?? (HOST_T.ko[key] ?? key);
}

function applyHostI18n(lang) {
  document.querySelectorAll("[data-i18n]").forEach((el) => {
    const key = el.getAttribute("data-i18n");
    if (!key) return;
    el.textContent = t(lang, key);
  });
}

function renderReviews(lang, reviews){
  const list = document.getElementById("reviewList");
  const empty = document.getElementById("reviewEmpty");
  if (!list || !empty) return;

  list.innerHTML = "";

  if (!reviews || reviews.length === 0){
    empty.style.display = "flex";
    return;
  }

  empty.style.display = "none";

  reviews.forEach(r => {
    const tripTitle = r.tripTitle ?? "Trip";
    const date = r.createdAt?.slice(0,10) ?? "-";
    const rating = r.rating ?? "-";
    const content = r.content ?? "";

    const card = document.createElement("div");
    card.className = "review-card";
    card.innerHTML = `
      <div class="review-top">
        <div class="review-title">${t(lang,"reviewTripLabel")}: ${escapeHtml(tripTitle)}</div>
        <div class="review-badge">${t(lang,"reviewRatingLabel")} ★ ${escapeHtml(String(rating))}</div>
      </div>
      <div class="review-meta">${t(lang,"reviewDateLabel")}: ${escapeHtml(date)}</div>
      <div class="review-content">${escapeHtml(content)}</div>
    `;
    list.appendChild(card);
  });
}

function escapeHtml(str){
  return String(str)
    .replaceAll("&","&amp;")
    .replaceAll("<","&lt;")
    .replaceAll(">","&gt;")
    .replaceAll('"',"&quot;")
    .replaceAll("'","&#39;");
}

document.addEventListener("DOMContentLoaded", () => {
  const sel = document.getElementById("langSelect");
  const lang = localStorage.getItem(LANG_KEY) || "ko";

  if (sel) sel.value = lang;

  applyHostI18n(lang);

  const reviews = window.__HOST_REVIEWS__ || [];
  renderReviews(lang, reviews);

  if (sel){
    sel.addEventListener("change", () => {
      localStorage.setItem(LANG_KEY, sel.value);
      applyHostI18n(sel.value);
      renderReviews(sel.value, window.__HOST_REVIEWS__ || []);
    });
  }
});

window.HOST_T = HOST_T;


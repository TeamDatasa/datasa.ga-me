console.log("[user-i18n] script loaded");

const LANG_KEY = "uiLang";

const USER_T = {
  ko: {
    back: "戻る",
    myInfo: "マイ情報",
    myInfoHint: "会員詳細情報ページです。",
    applicationStatus: "申請状況",
    applicationStatusMeta: "保留 / 承認 / 拒否",
    pending: "保留",
    approved: "承認",
    rejected: "拒否",
    myReviews: "マイレビュー",
    myReviewsMeta: "作成 / 修正 / 削除",
    myReviewsHint: "ツアー終了後にレビューを作成できます。",
    myTours: "参加したツアー",
    myToursHint: "承認済みのツアー一覧です。終了したツアーはレビューを作成できます。",
    viewAll: "すべて見る",
    myApplications: "申請履歴",
    myApplicationsHint: "最近申請したツアーの履歴です。",
    writeReview: "レビュー作成",
    editReview: "レビュー修正",
    deleteReview: "レビュー削除",
    chat: "チャット",
    emptyTours: "まだ承認されたツアーがありません。",
    emptyApplications: "申請したツアーがありません。"
  },

  ja: {
    back: "戻る",

    myInfo: "マイ情報",
    myInfoHint: "会員の詳細情報ページです。",

    applicationStatus: "申請状況",
    applicationStatusMeta: "待機 / 承認 / 却下",
    pending: "待機",
    approved: "承認",
    rejected: "却下",

    myReviews: "自分のレビュー",
    myReviewsMeta: "作成 / 修正 / 削除",
    myReviewsHint: "ツアー終了後にレビューを作成できます。",

    myTours: "参加したツアー",
    myToursHint: "承認されたツアー一覧です。終了したツアーはレビュー作成が可能です。",
    viewAll: "すべて見る",

    myApplications: "申請履歴",
    myApplicationsHint: "最近申請したツアーの履歴です。",

    writeReview: "レビュー作成",
    editReview: "レビュー修正",
    deleteReview: "レビュー削除",
    chat: "チャット",

    emptyTours: "承認されたツアーがまだありません。",
    emptyApplications: "申請したツアーがありません。"
  },

  en: {
    back: "Back",

    myInfo: "My Info",
    myInfoHint: "This is your account details page.",

    applicationStatus: "Application Status",
    applicationStatusMeta: "Pending / Approved / Rejected",
    pending: "Pending",
    approved: "Approved",
    rejected: "Rejected",

    myReviews: "My Reviews",
    myReviewsMeta: "Write / Edit / Delete",
    myReviewsHint: "You can write a review after the tour ends.",

    myTours: "My Tours",
    myToursHint: "Approved tours are listed here. Finished tours can be reviewed.",
    viewAll: "View all",

    myApplications: "My Applications",
    myApplicationsHint: "Your recent tour applications.",

    writeReview: "Write review",
    editReview: "Edit review",
    deleteReview: "Delete review",
    chat: "Chat",

    emptyTours: "No approved tours yet.",
    emptyApplications: "No applications yet."
  }
};

function applyUserI18n(lang) {
  const dict = USER_T[lang] || USER_T.ko;

  document.querySelectorAll("[data-i18n]").forEach((el) => {
    const key = el.getAttribute("data-i18n");
    if (!key) return;
    if (dict[key] != null) el.textContent = dict[key];
  });
}

document.addEventListener("DOMContentLoaded", () => {
  console.log("[user-i18n] DOMContentLoaded");

  const sel = document.getElementById("langSelect");
  if (!sel) {
    console.warn("[user-i18n] #langSelect not found");
    return;
  }

  const saved = localStorage.getItem(LANG_KEY) || "ko";
  sel.value = saved;
  applyUserI18n(saved);

  sel.addEventListener("change", () => {
    localStorage.setItem(LANG_KEY, sel.value);
    applyUserI18n(sel.value);
  });
});

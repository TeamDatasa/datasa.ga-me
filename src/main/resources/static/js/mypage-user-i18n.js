console.log("[user-i18n] script loaded");

const LANG_KEY = "uiLang";

const USER_T = {
  ko: {
    back: "뒤로",

    // page top
    myInfo: "내 정보",
    myInfoHint: "회원 상세 정보 페이지입니다.",

    // cards
    applicationStatus: "신청 현황",
    applicationStatusMeta: "대기 / 승인 / 거절",
    pending: "대기",
    approved: "승인",
    rejected: "거절",

    myReviews: "내 후기",
    myReviewsMeta: "작성 / 수정 / 삭제",
    myReviewsHint: "투어 종료 후 후기를 작성할 수 있습니다.",

    // panels
    myTours: "내가 참여한 투어",
    myToursHint: "승인된 투어 목록입니다. 종료된 투어는 후기 작성이 가능합니다.",
    viewAll: "전체 보기",

    myApplications: "내 신청 내역",
    myApplicationsHint: "최근 신청한 투어 내역입니다.",

    // actions
    writeReview: "후기 작성",
    editReview: "후기 수정",
    deleteReview: "후기 삭제",
    chat: "채팅",

    // empty
    emptyTours: "아직 승인된 투어가 없습니다.",
    emptyApplications: "신청한 투어가 없습니다."
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

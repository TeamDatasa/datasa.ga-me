// =========================
// i18n (page-only simple)
// =========================
const I18N = {
  ko: {
    pageTitle: "マイレビュー",
    pageHint: "ツアー終了後に作成できます。ツアーごとにレビューは1件まで作成可能です。",
    joined: "参加したツアー",
    joinedHint: "承認済みの参加ツアー一覧です。",
    empty: "承認されたツアーはまだありません。",
    reset: "リセット",

    write: "レビュー 作成",
    edit: "レビュー 修正",
    del: "レビュー 削除",

    rating: "評価",
    content: "内容",
    placeholder: "ツアー体験を入力してください...",

    cancel: "キャンセル",
    save: "保存",
    update: "修正",
    delete: "削除",

    deleteHint: "本当に削除しますか？削除すると元に戻せません。"
  },
  ja: {
    pageTitle: "私のレビュー",
    pageHint: "ツアー終了後に作成できます。ツアーごとにレビューは1件までです。",
    joined: "参加したツアー",
    joinedHint: "承認されて参加したツアー一覧です。",
    empty: "承認されたツアーはまだありません。",
    reset: "リセット",

    write: "レビュー作成",
    edit: "レビュー編集",
    del: "レビュー削除",

    rating: "評価",
    content: "内容",
    placeholder: "ツアーの体験を書いてください…",

    cancel: "キャンセル",
    save: "保存",
    update: "更新",
    delete: "削除",

    deleteHint: "本当に削除しますか？削除すると元に戻せません。"
  },
  en: {
    pageTitle: "My Reviews",
    pageHint: "You can write after the tour ends. One review per tour.",
    joined: "Joined Tours",
    joinedHint: "Approved tours you joined.",
    empty: "No approved tours yet.",
    reset: "Reset",

    write: "Write Review",
    edit: "Edit Review",
    del: "Delete Review",

    rating: "Rating",
    content: "Content",
    placeholder: "Write your experience...",

    cancel: "Cancel",
    save: "保存",
    update: "Update",
    delete: "Delete",

    deleteHint: "Delete this review? This action cannot be undone."
  }
};

function $(id) {
  return document.getElementById(id);
}

function applyLang(lang) {
  const t = I18N[lang] || I18N.ko;

  // 탭 タイトル
  document.title = t.pageTitle;

  // 페이지 헤더 텍스트
  const headTitle = document.querySelector("section.panel .panel-head h3");
  const headHint = document.querySelector("section.panel .panel-head .hint");
  const resetBtn = document.querySelector("section.panel .panel-head a.btn-ghost");
  if (headTitle) headTitle.textContent = t.pageTitle;
  if (headHint) headHint.textContent = t.pageHint;
  if (resetBtn) resetBtn.textContent = t.reset;

  // 목록 패널 첫 h3 / 첫 hint / empty
  const joinedTitle = document.querySelector(".grid .panel h3");
  const joinedHint = document.querySelector(".grid .panel .hint");
  const empty = document.querySelector(".empty");
  if (joinedTitle) joinedTitle.textContent = t.joined;
  if (joinedHint) joinedHint.textContent = t.joinedHint;
  if (empty) empty.textContent = t.empty;

  // 모달 라벨/placeholder/削除문구
  const labelRating = $("labelRating");
  const labelContent = $("labelContent");
  const textarea = $("modalContent");
  const deleteHint = $("deleteHint");
  if (labelRating) labelRating.textContent = t.rating;
  if (labelContent) labelContent.textContent = t.content;
  if (textarea) textarea.placeholder = t.placeholder;
  if (deleteHint) deleteHint.textContent = t.deleteHint;

  // 모달 キャンセル 버튼
  const cancelBtn = document.querySelector("#reviewModal .yw-modal__actions .btn-ghost");
  if (cancelBtn) cancelBtn.textContent = t.cancel;

  localStorage.setItem("mypage_lang", lang);
}

// =========================
// Modal logic (global funcs for onclick)
// =========================
let modal, form, titleEl, submitBtn;
let tripIdEl, reviewIdEl, ratingEl, contentEl;
let fieldsWrap, deleteBox;

function openReviewModal(mode, el) {
  // 言語 적용된 텍스트로 타이틀/버튼 표市
  const lang = $("langSelect")?.value || "ko";
  const t = I18N[lang] || I18N.ko;

  // reset
  tripIdEl.value = "";
  reviewIdEl.value = "";
  ratingEl.value = "5";
  contentEl.value = "";

  fieldsWrap.style.display = "block";
  deleteBox.style.display = "none";

  // 버튼 모양은 btn 유지, delete일 때만 btn-danger 추가
  submitBtn.classList.add("btn");
  submitBtn.classList.remove("btn-danger");

  if (mode === "new") {
    titleEl.textContent = t.write;
    form.action = "/mypage/reviews/create";
    tripIdEl.value = el.dataset.tripId || "";
    submitBtn.textContent = t.save;
  }

  if (mode === "edit") {
    titleEl.textContent = t.edit;
    form.action = "/mypage/reviews/update";
    tripIdEl.value = el.dataset.tripId || "";
    reviewIdEl.value = el.dataset.reviewId || "";
    submitBtn.textContent = t.update;

    // ✅ 자동 채움 (버튼에 data-rating / data-content 넣어둔 경우)
    if (el.dataset.rating) ratingEl.value = el.dataset.rating;
    if (el.dataset.content) contentEl.value = el.dataset.content;
  }

  if (mode === "delete") {
    titleEl.textContent = t.del;
    form.action = "/mypage/reviews/delete";
    reviewIdEl.value = el.dataset.reviewId || "";

    fieldsWrap.style.display = "none";
    deleteBox.style.display = "block";

    submitBtn.textContent = t.delete;
    submitBtn.classList.add("btn-danger");
  }

  modal.classList.add("is-open");
  modal.setAttribute("aria-hidden", "false");
}

function closeReviewModal() {
  modal.classList.remove("is-open");
  modal.setAttribute("aria-hidden", "true");
}

// ✅ HTML inline onclick에서 쓰려고 전역으로 노출
window.openReviewModal = openReviewModal;
window.closeReviewModal = closeReviewModal;

// =========================
// init
// =========================
document.addEventListener("DOMContentLoaded", () => {
  modal = $("reviewModal");
  form = $("reviewForm");
  titleEl = $("modalTitle");
  submitBtn = $("modalSubmitBtn");

  tripIdEl = $("modalTripId");
  reviewIdEl = $("modalReviewId");
  ratingEl = $("modalRating");
  contentEl = $("modalContent");

  fieldsWrap = $("modalFields");
  deleteBox = $("deleteBox");

  // overlay click close
  modal.addEventListener("click", (e) => {
    if (e.target === modal) closeReviewModal();
  });

  // ESC close
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeReviewModal();
  });

  // language init + change
  const select = $("langSelect");
  if (select) {
    const saved = localStorage.getItem("mypage_lang");
    if (saved && I18N[saved]) select.value = saved;

    applyLang(select.value);

    select.addEventListener("change", (e) => {
      applyLang(e.target.value);
    });
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("reviewForm");
  const submitBtn = document.getElementById("modalSubmitBtn");

  if (!form || !submitBtn) return;

  submitBtn.addEventListener("click", () => {
    console.log("[modal] submit click", form.action, new FormData(form));
  });

  form.addEventListener("submit", (e) => {
    console.log("[modal] submit fired", form.action);
  }, true);
});
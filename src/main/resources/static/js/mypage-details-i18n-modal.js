(() => {
  const LANG_KEY = "uiLang";

  const T = {
    ko: { modalWrite:"レビュー作成", modalEdit:"レビュー修正", modalDelete:"レビュー削除",
          rating:"評価", content:"内容", placeholder:"ツアー体験を入力してください...",
          cancel:"キャンセル", save:"保存", update:"修正", del:"削除",
          deleteHint:"本当に削除しますか？削除すると元に戻せません。" },
    ja: { modalWrite:"レビュー作成", modalEdit:"レビュー編集", modalDelete:"レビュー削除",
          rating:"評価", content:"内容", placeholder:"ツアーの体験を書いてください…",
          cancel:"キャンセル", save:"保存", update:"更新", del:"削除",
          deleteHint:"本当に削除しますか？削除すると元に戻せません。" },
    en: { modalWrite:"Write Review", modalEdit:"Edit Review", modalDelete:"Delete Review",
          rating:"Rating", content:"Content", placeholder:"Write your experience...",
          cancel:"Cancel", save:"保存", update:"Update", del:"Delete",
          deleteHint:"Delete this review? This action cannot be undone." }
  };

  const $ = (id) => document.getElementById(id);
  const curLang = () => $("langSelect")?.value || localStorage.getItem(LANG_KEY) || "ko";
  const dict = () => T[curLang()] || T.ko;

  // CSRF token (thymeleaf hidden input)
  function getCsrfToken() {
    // Spring Security 기본: name=_csrf 인 경우가 많음
    const input = document.querySelector('#reviewForm input[type="hidden"][name="_csrf"]');
    return input?.value || null;
  }

  async function apiFetch(url, { method = "GET", body } = {}) {
    const headers = {};
    const csrf = getCsrfToken();
    if (csrf) headers["X-CSRF-TOKEN"] = csrf;

    if (body != null) {
      headers["Content-Type"] = "application/json";
    }

    const res = await fetch(url, {
      method,
      headers,
      body: body != null ? JSON.stringify(body) : undefined,
      credentials: "same-origin"
    });

    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(`[${method}] ${url} -> ${res.status}\n${text}`);
    }

    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) return res.json();
    return null;
  }

  // =========================
  // Modal state
  // =========================
  const state = { mode: null };

  function applyModalI18n() {
    const t = dict();
    $("labelRating").textContent = t.rating;
    $("labelContent").textContent = t.content;
    $("modalContent").placeholder = t.placeholder;
    $("deleteHint").textContent = t.deleteHint;

    const cancelBtn = document.querySelector("#reviewModal .yw-modal__actions .btn-ghost");
    if (cancelBtn) cancelBtn.textContent = t.cancel;
  }

  async function openReviewModal(mode, el) {
    const t = dict();

    state.mode = mode;

    const modal = $("reviewModal");
    const form = $("reviewForm");
    const titleEl = $("modalTitle");
    const submitBtn = $("modalSubmitBtn");

    const tripIdEl = $("modalTripId");
    const reviewIdEl = $("modalReviewId");
    const ratingEl = $("modalRating");
    const contentEl = $("modalContent");

    const fieldsWrap = $("modalFields");
    const deleteBox = $("deleteBox");

    // reset
    tripIdEl.value = "";
    reviewIdEl.value = "";
    ratingEl.value = "5";
    contentEl.value = "";

    // 기본: 作成/修正 enabled + required
    ratingEl.disabled = false;
    contentEl.disabled = false;
    ratingEl.required = true;
    contentEl.required = true;

    fieldsWrap.style.display = "block";
    deleteBox.style.display = "none";

    submitBtn.classList.add("btn");
    submitBtn.classList.remove("btn-danger");

    // 안전: action 무市(우린 API만 씀)
    form.removeAttribute("action");

    if (mode === "new") {
      titleEl.textContent = t.modalWrite;
      submitBtn.textContent = t.save;
      tripIdEl.value = el.dataset.tripId || "";
    }

    if (mode === "edit") {
      titleEl.textContent = t.modalEdit;
      submitBtn.textContent = t.update;

      const tripId = el.dataset.tripId || "";
      tripIdEl.value = tripId;

      // ✅ 기존 内容 프리필: マイレビュー 조회
      if (tripId) {
        const me = await apiFetch(`/api/reviews/trips/${tripId}/me`, { method: "GET" });
        if (me?.reviewId != null) reviewIdEl.value = String(me.reviewId);
        if (me?.rating != null) ratingEl.value = String(me.rating);
        if (me?.content != null) contentEl.value = me.content;
      }
    }

    if (mode === "delete") {
      titleEl.textContent = t.modalDelete;
      submitBtn.textContent = t.del;
      submitBtn.classList.add("btn-danger");

      reviewIdEl.value = el.dataset.reviewId || "";

      // ✅ 削除는 required 검사 막기
      ratingEl.disabled = true;
      contentEl.disabled = true;
      ratingEl.required = false;
      contentEl.required = false;

      fieldsWrap.style.display = "none";
      deleteBox.style.display = "block";
    }

    applyModalI18n();

    modal.classList.add("is-open");
    modal.setAttribute("aria-hidden", "false");
  }

  function closeReviewModal() {
    const modal = $("reviewModal");
    modal.classList.remove("is-open");
    modal.setAttribute("aria-hidden", "true");
  }

  async function submitModal() {
    const mode = state.mode;

    const tripId = $("modalTripId").value;
    const reviewId = $("modalReviewId").value;
    const rating = Number($("modalRating").value);
    const content = $("modalContent").value;

    if (mode === "new") {
      await apiFetch("/api/reviews", {
        method: "POST",
        body: { tripId: Number(tripId), rating, content } // ✅ DTO 일치
      });
      window.location.href = "/mypage/details/user";
      return;
    }

    if (mode === "edit") {
      await apiFetch(`/api/reviews/${reviewId}`, {
        method: "PUT",
        body: { rating, content } // ✅ DTO 일치
      });
      window.location.href = "/mypage/details/user";
      return;
    }

    if (mode === "delete") {
      await apiFetch(`/api/reviews/${reviewId}`, { method: "DELETE" });
      window.location.href = "/mypage/details/user";
      return;
    }
  }

  // 전역(HTML onclick)
  window.openReviewModal = openReviewModal;
  window.closeReviewModal = closeReviewModal;

  document.addEventListener("DOMContentLoaded", () => {
    // lang init
    const sel = $("langSelect");
    if (sel) {
      const saved = localStorage.getItem(LANG_KEY) || "ko";
      sel.value = saved;
      sel.addEventListener("change", () => {
        localStorage.setItem(LANG_KEY, sel.value);
        applyModalI18n();
      });
    }
    applyModalI18n();

    // close handlers
    const modal = $("reviewModal");
    modal.addEventListener("click", (e) => {
      if (e.target === modal) closeReviewModal();
    });
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") closeReviewModal();
    });

    // submit handler
    const form = $("reviewForm");
    const submitBtn = $("modalSubmitBtn");

    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      try {
        submitBtn.disabled = true;
        await submitModal();
      } catch (err) {
        console.error(err);
        alert("処理中にエラーが発生しました。コンソールとサーバーログを確認してください。");
        submitBtn.disabled = false;
      }
    });
  });
})();
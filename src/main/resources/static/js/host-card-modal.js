(function () {
  const $ = (sel, root = document) => root.querySelector(sel);

  function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
  }

  function isBlank(v) {
    return v == null || String(v).trim().length === 0;
  }

  function genderKo(g) {
    if (!g) return null;
    if (g === "MALE") return "남성";
    if (g === "FEMALE") return "여성";
    if (g === "OTHER") return "기타";
    return g;
  }

  function countryKo(code2) {
    if (isBlank(code2)) return null;
    const code = String(code2).toUpperCase();
    try {
      const dn = new Intl.DisplayNames(["ko"], { type: "region" });
      return dn.of(code) || code;
    } catch (_) {
      return code;
    }
  }

  function openModal() {
    const overlay = $("#hostCardModalOverlay");
    if (!overlay) return;
    overlay.classList.add("is-open");
  }

  function closeModal() {
    const overlay = $("#hostCardModalOverlay");
    if (!overlay) return;
    overlay.classList.remove("is-open");
  }

  async function httpGetJson(url) {
    const fn = window.authFetch ? window.authFetch : fetch;
    const res = await fn(url, { method: "GET", redirectOn401: false });
    const text = await res.text();
    let data = null;
    try {
      data = text ? JSON.parse(text) : null;
    } catch (_) {}
    return { res, data, raw: text };
  }

  function setAvatar(url) {
    const img = $("#hcAvatar");
    if (!img) return;

    if (isBlank(url)) {
      img.removeAttribute("src");
      img.style.display = "none";
      return;
    }

    img.style.display = "";
    img.src = url;
  }

  function renderLoading() {
    const nameEl = $("#hcName");
    const metaEl = $("#hcMeta");
    const bioEl = $("#hcBio");
    if (nameEl) nameEl.textContent = "불러오는 중...";
    if (metaEl) metaEl.innerHTML = "";
    if (bioEl) bioEl.innerHTML = `<div class="hc-muted">프로필을 불러오는 중입니다.</div>`;
    setAvatar(null);
  }

  function renderError(message) {
    const nameEl = $("#hcName");
    const metaEl = $("#hcMeta");
    const bioEl = $("#hcBio");
    if (nameEl) nameEl.textContent = "호스트 프로필";
    if (metaEl) metaEl.innerHTML = "";
    if (bioEl) bioEl.innerHTML = `<div class="hc-muted">${escapeHtml(message)}</div>`;
    setAvatar(null);
  }

  function chip(label, className) {
    return `<span class="hc-chip ${className || ""}">${escapeHtml(label)}</span>`;
  }

  function renderCard(card) {
    const nameEl = $("#hcName");
    const metaEl = $("#hcMeta");
    const bioEl = $("#hcBio");

    const name = card?.name ?? "-";
    if (nameEl) nameEl.textContent = name;

    setAvatar(card?.profileImageUrl);

    const chips = [];

    const age = typeof card?.age === "number" && card.age > 0 ? `${card.age}세` : null;
    if (age) chips.push(chip(age, ""));

    const g = card?.gender ? String(card.gender) : null;
    const gKo = genderKo(g);
    if (gKo) {
      const cls = g === "FEMALE" ? "hc-chip--female" : g === "MALE" ? "hc-chip--male" : "";
      chips.push(chip(gKo, cls));
    }

    const country = countryKo(card?.countryCode);
    if (country) chips.push(chip(country, ""));

    const mbti = !isBlank(card?.mbti) ? String(card.mbti).toUpperCase() : null;
    if (mbti) chips.push(chip(mbti, ""));

    if (typeof card?.smoking === "boolean") {
      chips.push(chip(card.smoking ? "흡연" : "비흡연", card.smoking ? "hc-chip--warn" : "hc-chip--off"));
    }
    if (typeof card?.drinking === "boolean") {
      chips.push(chip(card.drinking ? "음주" : "비음주", card.drinking ? "hc-chip--warn" : "hc-chip--off"));
    }

    if (metaEl) metaEl.innerHTML = chips.join("");

    const bio = !isBlank(card?.bio) ? String(card.bio) : null;
    if (!bioEl) return;

    if (bio) {
      bioEl.style.display = "";
      bioEl.textContent = bio;
    } else {
      bioEl.style.display = "none";
      bioEl.textContent = "";
    }
  }

  async function openHostCard(hostId) {
    if (isBlank(hostId)) return;

    openModal();
    renderLoading();

    const { res, data } = await httpGetJson(`/api/hosts/${hostId}/card`);

    if (res.ok) {
      renderCard(data);
      return;
    }
    if (res.status === 404) {
      renderError("호스트 정보를 찾을 수 없습니다.");
      return;
    }
    renderError("프로필을 불러오지 못했습니다.");
  }

  function bind() {
    document.addEventListener(
        "click",
        (e) => {
          const trigger = e.target.closest("#hostCardOpen, [data-host-card-open]");
          if (!trigger) return;

          const ariaDisabled = trigger.getAttribute("aria-disabled");
          if (ariaDisabled === "true") return;

          const hostId = trigger.getAttribute("data-host-id");
          if (isBlank(hostId)) return;

          e.preventDefault();
          e.stopPropagation();
          if (typeof e.stopImmediatePropagation === "function") {
            e.stopImmediatePropagation();
          }

          openHostCard(hostId);
        },
        true
    );

    const overlay = $("#hostCardModalOverlay");
    if (overlay) {
      overlay.addEventListener("click", (e) => {
        if (e.target === overlay) closeModal();
      });
    }

    const closeBtn = $("#hostCardModalClose");
    if (closeBtn) closeBtn.addEventListener("click", closeModal);

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") closeModal();
    });
  }

  document.addEventListener("DOMContentLoaded", bind);
})();
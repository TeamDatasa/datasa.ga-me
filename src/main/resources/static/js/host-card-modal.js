(function () {
  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

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
      // 브라우저 지원 시: "AR" -> "아르헨티나"
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
    try { data = text ? JSON.parse(text) : null; } catch (_) {}
    return { res, data, raw: text };
  }

  function renderLoading() {
    $("#hcName").textContent = "불러오는 중...";
    $("#hcMeta").innerHTML = "";
    $("#hcBio").innerHTML = `<div class="hc-muted">프로필을 불러오는 중입니다.</div>`;
  }

  function renderError(message) {
    $("#hcName").textContent = "호스트 프로필";
    $("#hcMeta").innerHTML = "";
    $("#hcBio").innerHTML = `<div class="hc-muted">${escapeHtml(message)}</div>`;
  }

  function chip(label, className) {
    return `<span class="hc-chip ${className || ""}">${escapeHtml(label)}</span>`;
  }

  function renderCard(card) {
    const name = card?.name ?? "-";
    $("#hcName").textContent = name;

    const chips = [];

    // 나이: birthDate 미입력 -> null이면 숨김, 0/음수도 숨김
    const age = (typeof card?.age === "number" && card.age > 0) ? `${card.age}세` : null;
    if (age) chips.push(chip(age, ""));

    // 성별: 여자=붉은, 남자=푸른, 기타=기본
    const g = card?.gender ? String(card.gender) : null;
    const gKo = genderKo(g);
    if (gKo) {
      const cls = (g === "FEMALE") ? "hc-chip--female" : (g === "MALE") ? "hc-chip--male" : "";
      chips.push(chip(gKo, cls));
    }

    // 국가: countryCode가 없으면 숨김
    const country = countryKo(card?.countryCode);
    if (country) chips.push(chip(country, ""));

    // MBTI: 비어있으면 숨김
    const mbti = !isBlank(card?.mbti) ? String(card.mbti).toUpperCase() : null;
    if (mbti) chips.push(chip(mbti, ""));

    // 흡연/음주: null이면 숨김, true면 노랑, false면 off
    if (typeof card?.smoking === "boolean") {
      chips.push(chip(card.smoking ? "흡연" : "비흡연", card.smoking ? "hc-chip--warn" : "hc-chip--off"));
    }
    if (typeof card?.drinking === "boolean") {
      chips.push(chip(card.drinking ? "음주" : "비음주", card.drinking ? "hc-chip--warn" : "hc-chip--off"));
    }

    $("#hcMeta").innerHTML = chips.join("");

    // 자기소개: 비어있으면 안내 문구(이건 “미응답 숨김” 요구와 충돌 가능)
    // -> 요구사항 기준으로 "미응답이면 섹션 자체를 숨김" 처리
    const bio = !isBlank(card?.bio) ? String(card.bio) : null;
    if (bio) {
      $("#hcBio").style.display = "";
      $("#hcBio").textContent = bio;
    } else {
      $("#hcBio").style.display = "none";
      $("#hcBio").textContent = "";
    }
  }

  async function openHostCard(hostId) {
    if (!hostId) return;
    openModal();
    renderLoading();

    const { res, data } = await httpGetJson(`/api/hosts/${hostId}/card`);

    if (res.ok) { renderCard(data); return; }
    if (res.status === 403) { renderError("호스트 카드 프로필이 비공개입니다."); return; }
    if (res.status === 404) { renderError("호스트 정보를 찾을 수 없습니다."); return; }
    renderError("프로필을 불러오지 못했습니다.");
  }

  function bind() {
    // 열기 링크
    $$("#hostCardOpen, [data-host-card-open]").forEach((el) => {
      el.addEventListener("click", (e) => {
        e.preventDefault();
        e.stopPropagation(); // 카드 전체 클릭 이동 방지
        const hostId = el.getAttribute("data-host-id");
        openHostCard(hostId);
      });
    });

    // 닫기
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

console.log("[host-reviews] loaded");

function getUiLang(){
  return localStorage.getItem("uiLang") || "ko";
}

function tHost(key){
  const lang = getUiLang();
  // HOST_T는 mypage-host-i18n.js에 이미 있음
  const dict = (window.HOST_T && window.HOST_T[lang]) || (window.HOST_T && window.HOST_T.ko) || {};
  return dict[key] || key;
}

function formatDate(iso){
  if(!iso) return "-";
  // "2026-02-10T11:11:11" -> "2026-02-10"
  return String(iso).slice(0,10);
}

async function loadHostReviews(){
  const reviewListEl = document.getElementById("reviewList");
  const emptyEl = document.getElementById("reviewEmpty");
  if(!reviewListEl || !emptyEl) return;

  // ✅ 페이지에 있는 투어(tripId)들 수집
  const tripEls = Array.from(document.querySelectorAll(".tour-card[data-trip-id]"));
  const tripIds = tripEls
    .map(el => el.getAttribute("data-trip-id"))
    .filter(Boolean);

  // 투어가 없으면 후기 당연히 없음
  if(tripIds.length === 0){
    reviewListEl.innerHTML = "";
    emptyEl.style.display = "flex";
    return;
  }

  // ✅ 각 trip의 후기 호출 (병렬)
  const results = await Promise.allSettled(
    tripIds.map(id => fetch(`/api/reviews/trips/${id}`).then(r => r.ok ? r.json() : []))
  );

  const allReviews = results
    .filter(x => x.status === "fulfilled")
    .flatMap(x => Array.isArray(x.value) ? x.value : []);

  // ✅ 정렬(최신)
  allReviews.sort((a,b) => {
    const da = new Date(a.createdAt || 0).getTime();
    const db = new Date(b.createdAt || 0).getTime();
    return db - da;
  });

  reviewListEl.innerHTML = "";

  if(allReviews.length === 0){
    emptyEl.style.display = "flex";
    return;
  }

  emptyEl.style.display = "none";

  allReviews.forEach(r => {
    const card = document.createElement("div");
    card.className = "review-card";

    const reviewId = r.reviewId; // ✅ ReviewResponse에 있어야 함
      if (reviewId) {
        card.style.cursor = "pointer";
        card.addEventListener("click", () => {
          location.href = `/reviews/${reviewId}`;   // ✅ 너가 만들 상세 페이지 라우트
          // 만약 마이페이지 내 모달/모드로 가고 싶으면:
          // location.href = `/mypage/reviews?mode=edit&reviewId=${reviewId}`;
        });
      }

    const tripTitle = r.tripTitle || r.title || "Trip";
    const content = r.content || "";
    const rating = (r.rating ?? "-");
    const date = formatDate(r.createdAt);

    card.innerHTML = `
      <div class="review-top">
        <div class="review-title">
          <span class="muted">${tHost("reviewTripLabel")}:</span>
          ${escapeHtml(tripTitle)}
        </div>
        <div class="review-badge">
          ${tHost("reviewRatingLabel")} ★
          <b>${escapeHtml(String(rating))}</b>
        </div>
      </div>
      <div class="review-meta">
        ${tHost("reviewDateLabel")}: ${escapeHtml(date)}
      </div>
      <div class="review-content">${escapeHtml(content)}</div>
    `;


    reviewListEl.appendChild(card);
  });
}

// XSS 방지용
function escapeHtml(str){
  return String(str)
    .replaceAll("&","&amp;")
    .replaceAll("<","&lt;")
    .replaceAll(">","&gt;")
    .replaceAll('"',"&quot;")
    .replaceAll("'","&#039;");
}

document.addEventListener("DOMContentLoaded", () => {
  loadHostReviews();

  // ✅ 언어 바꿀 때도 후기 텍스트(Trip/Date/Rating) 다시 적용되게
  const sel = document.getElementById("langSelect");
  if(sel){
    sel.addEventListener("change", () => {
      // i18n 적용 후 후기 다시 렌더
      loadHostReviews();
    });
  }
});

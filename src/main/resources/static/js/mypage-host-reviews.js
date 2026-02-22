console.log("[host-reviews] loaded");

function getUiLang(){
  return localStorage.getItem("uiLang") || "ko";
}

function tHost(key){
  const lang = getUiLang();
  const dict = (window.HOST_T && window.HOST_T[lang]) || (window.HOST_T && window.HOST_T.ko) || {};
  return dict[key] || key;
}

function formatDate(iso){
  if(!iso) return "-";
  return String(iso).slice(0,10);
}

async function loadHostReviews(){
  const reviewListEl = document.getElementById("reviewList");
  const emptyEl = document.getElementById("reviewEmpty");
  if(!reviewListEl || !emptyEl) return;

  const tripEls = Array.from(document.querySelectorAll(".tour-card[data-trip-id]"));
  const tripIds = tripEls
    .map(el => el.getAttribute("data-trip-id"))
    .filter(Boolean);

  if(tripIds.length === 0){
    reviewListEl.innerHTML = "";
    emptyEl.style.display = "flex";
    return;
  }

  const results = await Promise.allSettled(
    tripIds.map(id => fetch(`/api/reviews/trips/${id}`).then(r => r.ok ? r.json() : []))
  );

  const allReviews = results
    .filter(x => x.status === "fulfilled")
    .flatMap(x => Array.isArray(x.value) ? x.value : []);

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

    const reviewId = r.reviewId;
      if (reviewId) {
        card.style.cursor = "pointer";
        card.addEventListener("click", () => {
          location.href = `/reviews/${reviewId}`;
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

  const sel = document.getElementById("langSelect");
  if(sel){
    sel.addEventListener("change", () => {
      loadHostReviews();
    });
  }
});

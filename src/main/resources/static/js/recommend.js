document.addEventListener("DOMContentLoaded", () => {
  console.log("🔥 recommend.js loaded");

  fetch("/api/recommendations?limit=3")
    .then(res => {
      console.log("status:", res.status);
      if (!res.ok) throw new Error("HTTP " + res.status);
      return res.json();
    })
    .then(data => {
      console.log("recommendation data:", data);

      const titleEl = document.getElementById("recommendTitle");
      const listEl = document.getElementById("recommendList");

      if (!titleEl || !listEl) {
        console.error("❌ recommendTitle / recommendList 없음");
        return;
      }

      listEl.innerHTML = "";





      // ✅ 핵심: data 자체가 배열
      if (!Array.isArray(data) || data.length === 0) {
        titleEl.innerText = "추천 코스";
        listEl.innerHTML = emptyCard();
        return;
      }

      // 타이틀
      titleEl.innerText = "추천 코스";

      // 카드 렌더링
      data.forEach(trip => {
        listEl.insertAdjacentHTML("beforeend", tripCard(trip));
      });
    })
    .catch(err => {
      console.error("❌ 추천 로딩 실패:", err);

      const titleEl = document.getElementById("recommendTitle");
      const listEl = document.getElementById("recommendList");

      if (titleEl) titleEl.innerText = "추천 코스";
      if (listEl) listEl.innerHTML = emptyCard();
    });
});

/* ======================
   카드 템플릿
====================== */

function tripCard(trip) {
  return `
    <article class="yw-card">
      <div class="yw-card__thumb"></div>

      <div class="yw-card__body">
        <!-- 여행 제목 -->
        <div class="yw-card__title">
          ${trip.title}
        </div>

        <!-- 작성자 -->
        <div class="yw-card__author">
          👤 ${trip.hostName}
        </div>

        <!-- 메타 정보 -->
        <div class="yw-card__meta">
          <span> ${trip.region}</span>
         <span> ${trip.currentApplicants} / ${trip.maxParticipants}</span>
        </div>
      </div>
    </article>
  `;
}


function emptyCard() {
  return `
    <article class="yw-card">
      <div class="yw-card__thumb"></div>
      <div class="yw-card__body">
        <div class="yw-card__title">추천 코스 준비 중</div>
        <div class="yw-card__meta">곧 AI 추천이 제공됩니다</div>
      </div>
    </article>
  `;
}

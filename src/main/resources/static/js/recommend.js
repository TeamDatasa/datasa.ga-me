document.addEventListener("DOMContentLoaded", () => {
  console.log(" recommend.js loaded");

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

      if (!listEl) {
        console.error("❌ recommendTitle / recommendList 없음");
        return;
      }

      listEl.innerHTML = "";





      // ✅ 핵심: data 자체가 배열
      if (!Array.isArray(data) || data.length === 0) {
        if(titleEl)titleEl.innerText = "おすすめコース";
        listEl.innerHTML = emptyCard();
        return;
      }

      // 타이틀
      if(!titleEl)titleEl.innerText = "おすすめコース";

      // 카드 렌더링
      data.forEach(trip => {
        listEl.insertAdjacentHTML("beforeend", tripCard(trip));
      });
    })
    .catch(err => {
      console.error("❌ おすすめの読み込みに失敗:", err);

      const titleEl = document.getElementById("recommendTitle");
      const listEl = document.getElementById("recommendList");

      if (titleEl) titleEl.innerText = "おすすめコース";
      if (listEl) listEl.innerHTML = emptyCard();
    });
});

/* ======================
   카드 템플릿
====================== */

function tripCard(trip) {
  return `
    <article class="yw-card">
      <div class="yw-card__thumb">
       <span class="yw-badge ai">AIおすすめ</span>
       </div>

      <div class="yw-card__body">
        <!-- 旅行 タイトル -->
        <div class="yw-card__title">
          ${trip.title}
        </div>

        <!-- 메타 정보 -->
        <div class="yw-card__meta">
          <span>${REGION_KR_MAP[trip.region] || trip.region}</span>
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
        <div class="yw-card__title">おすすめコース準備中</div>
        <div class="yw-card__meta">まもなくAIおすすめが提供されます</div>
      </div>
    </article>
  `;
}

const REGION_KR_MAP = {
  SEOUL: "ソウル",
  BUSAN: "釜山",
  JEJU: "済州",
  DAEGU: "大邱",
  INCHEON: "仁川",
  GYEONGJU: "경주",
  GANGWON: "강원",
  JEONJU: "전주",
  YEOSU: "여수",
  SUWON: "수원",
  DAEJEON: "大田",
  POHANG: "포항",
  GWANGJU: "光州",
  CHUNCHEON: "춘천",
  ANDONG: "안동",
  SEJONG: "世宗",
  ULSAN: "蔚山",
  GEOJE: "거제",
  TONGYEONG: "통영",
  CHANGWON: "창원",
  MOKPO: "목포",
  SUNCHEON: "순천",
  GIMHAE: "김해",
  YANGSAN: "양산",
  CHEONAN: "천안",
  CHUNGJU: "충주",
  JECHEON: "제천",
  WONJU: "원주",
  NAJU: "나주",
  GUNSAN: "군산",
  IKSAN: "익산",
  GUMI: "구미",
  PAJU: "파주",
  GAPYEONG: "가평"
};


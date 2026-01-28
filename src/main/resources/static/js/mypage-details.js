document.addEventListener("DOMContentLoaded", () => {
  const role = localStorage.getItem("role");
  const el = document.getElementById("detailsRoleHint");
  if (el) el.textContent = role ? `Current role: ${role}` : "Role unknown.";
});

function openReviewCreate(btn){
  const tripId = btn.dataset.tripId;
  const title = btn.dataset.tripTitle || "Trip";

  document.getElementById("reviewId").value = "";
  document.getElementById("reviewTripId").value = tripId;
  document.getElementById("reviewRating").value = "5";
  document.getElementById("reviewContent").value = "";

  document.getElementById("reviewModalTitle").innerText = "Write Review";
  document.getElementById("reviewModalSub").innerText = title;
  document.getElementById("reviewHint").innerText = "";

  showReviewModal();
}

function openReviewEdit(btn){
  const reviewId = btn.dataset.reviewId;
  const title = btn.dataset.tripTitle || "Trip";

  document.getElementById("reviewId").value = reviewId;
  document.getElementById("reviewTripId").value = ""; // 수정은 tripId 필요 없을 수도
  document.getElementById("reviewModalTitle").innerText = "Edit Review";
  document.getElementById("reviewModalSub").innerText = title;
  document.getElementById("reviewHint").innerText = "TODO: load existing review content via API";

  // 임시 값
  document.getElementById("reviewRating").value = "5";
  document.getElementById("reviewContent").value = "";

  showReviewModal();
}

function showReviewModal(){
  const m = document.getElementById("reviewModal");
  m.setAttribute("aria-hidden","false");
}

function closeReviewModal(){
  const m = document.getElementById("reviewModal");
  m.setAttribute("aria-hidden","true");
}

async function submitReview(e){
  e.preventDefault();

  const reviewId = document.getElementById("reviewId").value;
  const tripId = document.getElementById("reviewTripId").value;
  const rating = Number(document.getElementById("reviewRating").value);
  const content = document.getElementById("reviewContent").value;

  // API 붙이기 전 임시
  console.log("submit", {reviewId, tripId, rating, content});
  document.getElementById("reviewHint").innerText = "Saved (stub). Connect API next.";

  // TODO API 연결 예시:
  // if (!reviewId) POST /api/reviews {tripId, rating, content}
  // else PUT /api/reviews/{reviewId} {rating, content}

  return false;
}

async function deleteReview(btn){
  const reviewId = btn.dataset.reviewId;
  if(!confirm("Delete this review?")) return;

  console.log("delete", {reviewId});
  alert("Deleted (stub). Connect API next.");

  // TODO API 연결: DELETE /api/reviews/{reviewId}
}


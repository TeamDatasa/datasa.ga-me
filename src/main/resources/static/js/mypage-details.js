
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
  document.getElementById("reviewTripId").value = "";
  document.getElementById("reviewModalTitle").innerText = "Edit Review";
  document.getElementById("reviewModalSub").innerText = title;
  document.getElementById("reviewHint").innerText = "TODO: load existing review content via API";

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

  console.log("submit", {reviewId, tripId, rating, content});
  document.getElementById("reviewHint").innerText = "Saved (stub). Connect API next.";

  return false;
}

async function deleteReview(btn){
  const reviewId = btn.dataset.reviewId;
  if(!confirm("Delete this review?")) return;

  console.log("delete", {reviewId});
  alert("Deleted (stub). Connect API next.");

}


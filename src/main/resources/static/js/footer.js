document.addEventListener("DOMContentLoaded", function () {
  const btn = document.querySelector("[data-top-btn]");
  if (!btn) return;

  btn.addEventListener("click", function () {
    window.scrollTo({ top: 0, behavior: "smooth" });
  });
});

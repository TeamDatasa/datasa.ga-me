document.addEventListener("DOMContentLoaded", () => {
  const guestMenu = document.getElementById("guestMenu");
  const userMenu = document.getElementById("userMenu");
  const logoutBtn = document.getElementById("logoutBtn");
  const toast = document.getElementById("loginToast");

  if (!guestMenu || !userMenu) return;

  // ✅ 토스트 (DOMContentLoaded 안으로 넣는 게 안전)
  if (sessionStorage.getItem("justLoggedIn") === "1") {
    sessionStorage.removeItem("justLoggedIn");
    toast?.classList.add("is-show");
    setTimeout(() => toast?.classList.remove("is-show"), 2000);
  }

  const token = localStorage.getItem("accessToken");

  if (token && token.trim()) {
    guestMenu.style.display = "none";
    userMenu.style.display = "flex";
  } else {
    guestMenu.style.display = "flex";
    userMenu.style.display = "none";
  }

  logoutBtn?.addEventListener("click", () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    window.location.href = "/";
  });
});

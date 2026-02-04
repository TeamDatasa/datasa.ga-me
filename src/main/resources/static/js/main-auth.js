document.addEventListener("DOMContentLoaded", async () => {
  const guestMenu = document.getElementById("guestMenu");
  const userMenu = document.getElementById("userMenu");
  const logoutBtn = document.getElementById("logoutBtn");
  const toast = document.getElementById("loginToast");

  if (!guestMenu || !userMenu) return;

  // ✅ 로그인 직후 토스트
  if (sessionStorage.getItem("justLoggedIn") === "1") {
    sessionStorage.removeItem("justLoggedIn");
    toast?.classList.add("is-show");
    setTimeout(() => toast?.classList.remove("is-show"), 2000);
  }

  // ✅ 로그인 여부 확인 (쿠키 기반 JWT → me API로 판단)
  let isLogin = false;
  try {
    const res = await fetch("/api/auth/api/auth/me", {
      method: "GET",
      credentials: "include", // ⭐ 쿠키 필수
    });
    isLogin = res.ok;
  } catch (e) {
    isLogin = false;
  }

  if (isLogin) {
    guestMenu.style.display = "none";
    userMenu.style.display = "flex";
  } else {
    guestMenu.style.display = "flex";
    userMenu.style.display = "none";
  }

  // ✅ 로그아웃 (서버에서 쿠키 만료)
  logoutBtn?.addEventListener("click", async () => {
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (e) {
      // 실패해도 강제 이동
    }
    window.location.href = "/";
  });
});

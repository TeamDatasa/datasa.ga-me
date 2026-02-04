document.addEventListener("DOMContentLoaded", async () => {
  const guestMenu = document.getElementById("guestMenu");
  const userMenu = document.getElementById("userMenu");
  const logoutBtn = document.getElementById("logoutBtn");
  const toast = document.getElementById("loginToast");

  if (!guestMenu || !userMenu) return;

  const setUI = (isLogin) => {
    guestMenu.style.display = isLogin ? "none" : "flex";
    userMenu.style.display = isLogin ? "flex" : "none";
  };

  if (sessionStorage.getItem("justLoggedIn") === "1") {
    sessionStorage.removeItem("justLoggedIn");
    toast?.classList.add("is-show");
    setTimeout(() => toast?.classList.remove("is-show"), 2000);
  }

  let isLogin = false;
  try {
    const res = await fetch("/api/auth/me", {
      method: "GET",
      credentials: "include",
      cache: "no-store",
    });
    isLogin = res.ok;
  } catch (_) {
    isLogin = false;
  }

  setUI(isLogin);

  logoutBtn?.addEventListener("click", async (e) => {
    e.preventDefault();

    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (_) {}

    setUI(false);
    window.location.href = "/";
  });
});

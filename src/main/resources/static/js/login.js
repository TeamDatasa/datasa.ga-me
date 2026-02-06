document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email")?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";

    if (!email) return alert("Please enter your email address.");
    if (!password) return alert("Please enter your password.");

    try {
    const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      });

      const raw = await res.text();
      const contentType = res.headers.get("content-type") || "";
      const data =
        raw && contentType.includes("application/json") ? JSON.parse(raw) : null;

      if (!res.ok) {
        throw new Error(data?.message || raw || `Sign in failed. (${res.status})`);
      }

      sessionStorage.setItem("justLoggedIn", "1");
      window.location.href = "/";
    } catch (err) {
      alert(err?.message || "Sign in failed.");
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email")?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";

    if (!email) return alert("Please enter your email address.");
    if (!password) return alert("Please enter your password.");

    const payload = { email, password };

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload),
      });

      const contentType = response.headers.get("content-type") || "";
      const raw = await response.text();

      console.log("LOGIN status:", response.status);
      console.log("LOGIN content-type:", contentType);
      console.log("LOGIN raw:", raw);

      let data = null;
      if (raw && contentType.includes("application/json")) {
        try {
          data = JSON.parse(raw);
        } catch (err) {
          console.error("LOGIN JSON parse error:", err);
        }
      }

      if (!response.ok) {
        const errorMessage = data?.message || data?.error || raw || "Sign in failed.";
        throw new Error(errorMessage);
      }

      const token = data?.accessToken || data?.token || data?.access_token;

      if (token && token.trim()) {
        localStorage.setItem("accessToken", token);
      } else {
        console.warn("⚠️ No token in response body. Maybe set via cookie only.");
      }

      if (data?.userId != null) localStorage.setItem("userId", String(data.userId));
      if (data?.email) localStorage.setItem("email", data.email);
      if (data?.role) localStorage.setItem("role", data.role);

      console.log("✅ saved token:", localStorage.getItem("accessToken"));

      sessionStorage.setItem("justLoggedIn", "1");
      window.location.href = "/";


    } catch (error) {
      alert(error?.message || "Sign in failed.");
    }
  });
});

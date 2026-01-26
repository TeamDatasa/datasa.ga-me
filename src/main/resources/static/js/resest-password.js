document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(location.search);
  const token = params.get("token");

  const msg = document.getElementById("msg");
  const form = document.getElementById("resetForm");

  if (!token) {
    msg.textContent = "Invalid reset link.";
    form.style.display = "none";
    return;
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    msg.textContent = "";

    const p1 = document.getElementById("p1").value;
    const p2 = document.getElementById("p2").value;

    if (p1 !== p2) return (msg.textContent = "Passwords do not match.");
    if (p1.length < 8) return (msg.textContent = "Password must be at least 8 characters.");

    const res = await fetch("/api/auth/password/reset", {
      method: "POST",
      headers: {"Content-Type":"application/json"},
      body: JSON.stringify({ token, newPassword: p1 })
    });

    if (res.ok) {
      msg.textContent = "Password reset successful. Redirecting...";
      setTimeout(() => window.location.href = "/auth/login", 700);
    } else {
      const text = await res.text().catch(() => "");
      msg.textContent = text || "Reset failed. The link may be expired.";
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(location.search);
  const preset = params.get("email");
  if (preset) document.getElementById("email").value = preset;

  const form = document.getElementById("forgotForm");
  const msg = document.getElementById("msg");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    msg.textContent = "";

    const email = document.getElementById("email").value.trim();

    await fetch("/api/auth/password/reset-request", {
      method: "POST",
      headers: {"Content-Type":"application/json"},
      body: JSON.stringify({ email })
    });

    msg.textContent = "If the email exists, a reset link has been sent.";
  });
});

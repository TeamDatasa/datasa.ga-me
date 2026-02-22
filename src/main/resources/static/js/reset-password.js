document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("resetForm");
  const msg = document.getElementById("msg");

  if (!form) return;

  const params = new URLSearchParams(window.location.search);
  const token = params.get("token") || "";

  if (!token) {
    if (msg) msg.innerText = "유효하지 않은 링크입니다. (token 없음)";
    return;
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const p1 = document.getElementById("p1")?.value ?? "";
    const p2 = document.getElementById("p2")?.value ?? "";

    if (p1.length < 8) return alert("비밀번호는 8자 이상이어야 합니다.");
    if (p1 !== p2) return alert("비밀번호가 일치하지 않습니다.");

    try {
      const res = await fetch("/api/auth/password/reset", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token, newPassword: p1 }),
      });

      const raw = await res.text();

      if (!res.ok) {
        throw new Error(raw || `비밀번호 변경 실패 (${res.status})`);
      }

      if (msg) msg.innerText = "비밀번호 변경 완료! 다시 로그인해 주세요.";

      document.cookie = "access_token=; Path=/; Max-Age=0; SameSite=Lax";
      document.cookie = "JSESSIONID=; Path=/; Max-Age=0;";

      setTimeout(() => {
        window.location.href = "/auth/login";
      }, 500);

    } catch (err) {
      alert(err?.message || "비밀번호 변경 실패");
    }
  });
});

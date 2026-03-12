document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("resetForm");
  const msg = document.getElementById("msg");

  if (!form) return;

  const params = new URLSearchParams(window.location.search);
  const token = params.get("token") || "";

  if (!token) {
    if (msg) msg.innerText = "無効なリンクです。（token がありません）";
    return;
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const p1 = document.getElementById("p1")?.value ?? "";
    const p2 = document.getElementById("p2")?.value ?? "";

    if (p1.length < 8) return alert("パスワードは8文字以上である必要があります。");
    if (p1 !== p2) return alert("パスワードが一致しません。");

    try {
      const res = await fetch("/api/auth/password/reset", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token, newPassword: p1 }),
      });

      const raw = await res.text();

      if (!res.ok) {
        throw new Error(raw || `パスワードの変更に失敗しました。 (${res.status})`);
      }

      if (msg) msg.innerText = "パスワード 변경 완료! 다市 ログイン해 ください.";

      document.cookie = "access_token=; Path=/; Max-Age=0; SameSite=Lax";
      document.cookie = "JSESSIONID=; Path=/; Max-Age=0;";

      setTimeout(() => {
        window.location.href = "/auth/login";
      }, 500);

    } catch (err) {
      alert(err?.message || "パスワードの変更に失敗しました。");
    }
  });
});

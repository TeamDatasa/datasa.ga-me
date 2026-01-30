
document.getElementById("resetRequestForm").addEventListener("submit", async e => {
  e.preventDefault();

  const email = document.getElementById("email").value;

  const res = await fetch("/api/auth/password/reset-request", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email })
  });

  if (res.ok) {
    document.getElementById("msg").innerText =
      "재설정 링크가 이메일로 전송되었습니다.";
  } else {
    document.getElementById("msg").innerText =
      "이메일을 확인해 주세요.";
  }
});

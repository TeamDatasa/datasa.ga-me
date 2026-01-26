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
      // 로그인 API 호출
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const contentType = response.headers.get("content-type") || "";
        let errorMessage = "Sign in failed.";

        if (contentType.includes("application/json")) {
          const errorData = await response.json().catch(() => null);
          errorMessage = errorData?.message || errorData?.error || errorMessage;
        }
        else {
          const text = await response.text().catch(() => "");
          if (text) errorMessage = text;
        }

        throw new Error(errorMessage);
      }

      // 성공 응답(JSON) 파싱
      const data = await response.json();

      // JWT 토큰 저장 (필수)
      // AuthResponse: { accessToken, userId, email, role }
      localStorage.setItem("accessToken", data.accessToken);

      // (선택) 화면 분기/표시용으로 같이 저장해두면 편함
      localStorage.setItem("userId", String(data.userId));
      localStorage.setItem("email", data.email);
      localStorage.setItem("role", data.role);

      // 로그인 성공 처리
      alert("Signed in successfully!");
      window.location.href = "/mypage";

    } catch (error) {
      alert(error?.message || "Sign in failed.");
    }
  });
});

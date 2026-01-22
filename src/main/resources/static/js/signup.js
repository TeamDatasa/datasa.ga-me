document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("signupForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email")?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";
    const passwordConfirm = document.getElementById("passwordConfirm")?.value ?? "";
    const nickname = document.getElementById("nickname")?.value?.trim();
    const roleElement = document.querySelector('input[name="role"]:checked');
    const role = roleElement ? roleElement.value : null;

    if (!email) return alert("Please enter your email address.");
    if (!password) return alert("Please enter your password.");
    if (!passwordConfirm) return alert("Please confirm your password.");
    if (password !== passwordConfirm) return alert("Passwords do not match.");
    if (!nickname) return alert("Please enter your nickname.");
    if (!role) return alert("Please select a user role.");

    const payload = {
      email: email,
      password: password,
      nickname: nickname,
      role: role,
    };

    try {
      // 회원가입 API 호출
      const response = await fetch("/api/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      // 응답이 실패인 경우
      if (!response.ok) {
        const contentType = response.headers.get("content-type") || "";
        let errorMessage = "Sign up failed.";

        // JSON 에러 응답 처리
        if (contentType.includes("application/json")) {
          const errorData = await response.json().catch(() => null);
          errorMessage = errorData?.message || errorData?.error || errorMessage;
        }
        // 텍스트 에러 응답 처리
        else {
          const text = await response.text().catch(() => "");
          if (text) errorMessage = text;
        }

        throw new Error(errorMessage);
      }

      // 회원가입 성공 처리
      alert("Account created successfully!");
      window.location.href = "/login";

    } catch (error) {
      // 회원가입 실패 시 에러 메시지 표시
      alert(error?.message || "Sign up failed.");
    }
  });
});

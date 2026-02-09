// ============================
// 국가 텍스트 -> 코드 변환
// ============================
function countryNameToCode(name) {
  if (!name) return null;

  const n = name.trim().toLowerCase();

  const map = {
    // KR
    "korea": "KR",
    "대한민국": "KR",
    "한국": "KR",
    "south korea": "KR",
    "republic of korea": "KR",

    // JP
    "japan": "JP",
    "일본": "JP",

    // US
    "usa": "US",
    "united states": "US",
    "united states of america": "US",
    "미국": "US",

    // CN (필요 없으면 지워도 됨)
    "china": "CN",
    "중국": "CN",
  };

  return map[n] || null;
}

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("signupForm");
  if (!form) return;

  const emailInput = document.getElementById("email");
  const sendCodeBtn = document.getElementById("sendCodeBtn");
  const verifyCodeBtn = document.getElementById("verifyCodeBtn");
  const codeInput = document.getElementById("verificationCode");
  const statusEl = document.getElementById("emailVerifyStatus");
  const signupBtn = document.getElementById("signupBtn");

  const birthDateInput = document.getElementById("birthDate");
  const countryInput = document.getElementById("country"); // ✅ 텍스트 input/또는 select 둘 다 가능
  const regionInput = document.getElementById("region");

  // ✅ 모달 요소
  const mailModal = document.getElementById("mailModal");
  const mailModalTitle = document.getElementById("mailModalTitle");
  const mailModalMsg = document.getElementById("mailModalMsg");
  const mailModalClose = document.getElementById("mailModalClose");

  const openMailModal = (title, msg) => {
    if (mailModalTitle) mailModalTitle.textContent = title;
    if (mailModalMsg) mailModalMsg.textContent = msg;
    if (mailModal) mailModal.classList.remove("hidden");
  };

  const closeMailModal = () => {
    if (mailModal) mailModal.classList.add("hidden");
  };

  if (mailModalClose) mailModalClose.addEventListener("click", closeMailModal);

  let isEmailVerified = false;

  const setStatus = (text) => {
    if (statusEl) statusEl.textContent = text;
  };

  const setVerifiedState = (verified) => {
    isEmailVerified = verified;

    if (signupBtn) signupBtn.disabled = !verified;

    if (verified) {
      setStatus("✅ 이메일 인증이 완료되었습니다.");
      if (sendCodeBtn) sendCodeBtn.disabled = true;
      if (verifyCodeBtn) verifyCodeBtn.disabled = true;
      if (emailInput) emailInput.readOnly = true;
      if (codeInput) codeInput.readOnly = true;
    }
  };

  if (emailInput) {
    emailInput.addEventListener("input", () => {
      isEmailVerified = false;
      if (signupBtn) signupBtn.disabled = true;
      if (sendCodeBtn) sendCodeBtn.disabled = false;
      if (verifyCodeBtn) verifyCodeBtn.disabled = false;
      if (emailInput) emailInput.readOnly = false;
      if (codeInput) codeInput.readOnly = false;
      setStatus("");
    });
  }

  // ============================
  // 인증 코드 전송
  // ============================
  if (sendCodeBtn) {
    sendCodeBtn.addEventListener("click", async () => {
      const email = emailInput?.value?.trim();
      if (!email) {
        alert("이메일 주소를 입력해 주세요.");
        return;
      }

      openMailModal(
        "인증 메일 전송 안내",
        "인증 메일을 전송했습니다. 메일 도착까지 몇 분 정도 소요될 수 있으며, 스팸함도 함께 확인해 주세요."
      );

      try {
        const res = await fetch("/api/auth/email/send-code", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email }),
        });

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || "인증 코드 전송에 실패했습니다.");
        }

        setStatus("인증 코드가 전송되었습니다. 이메일을 확인해 주세요.");
      } catch (err) {
        openMailModal(
          "전송 실패",
          err?.message || "인증 코드 전송에 실패했습니다. 잠시 후 다시 시도해 주세요."
        );
      }
    });
  }

  // ============================
  // 인증 코드 확인
  // ============================
  if (verifyCodeBtn) {
    verifyCodeBtn.addEventListener("click", async () => {
      const email = emailInput?.value?.trim();
      const code = codeInput?.value?.trim();

      if (!email) {
        alert("이메일 주소를 입력해 주세요.");
        return;
      }
      if (!code) {
        alert("6자리 인증 코드를 입력해 주세요.");
        return;
      }
      if (!/^\d{6}$/.test(code)) {
        alert("인증 코드는 6자리 숫자여야 합니다.");
        return;
      }

      try {
        const res = await fetch("/api/auth/email/verify-code", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, code }),
        });

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || "인증 코드가 올바르지 않거나 만료되었습니다.");
        }

        setVerifiedState(true);
        alert("이메일 인증이 완료되었습니다.");
      } catch (err) {
        alert(err?.message || "인증 코드가 올바르지 않거나 만료되었습니다.");
      }
    });
  }

  // ============================
  // 회원가입 제출
  // ============================
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = emailInput?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";
    const passwordConfirm = document.getElementById("passwordConfirm")?.value ?? "";
    const name = document.getElementById("name")?.value?.trim();

    const role = document.querySelector('input[name="role"]:checked')?.value ?? null;

    const birthDate = birthDateInput?.value || "";
    const gender = document.querySelector('input[name="gender"]:checked')?.value ?? null;

    // ✅ 국가 텍스트 입력 -> 코드 변환
    const countryName = countryInput?.value?.trim() || "";
    const countryCode = countryNameToCode(countryName);

    const region = regionInput?.value?.trim() || "";

    if (!email) return alert("이메일 주소를 입력해 주세요.");
    if (!password) return alert("비밀번호를 입력해 주세요.");
    if (!passwordConfirm) return alert("비밀번호 확인을 입력해 주세요.");
    if (password !== passwordConfirm) return alert("비밀번호가 일치하지 않습니다.");
    if (!name) return alert("닉네임을 입력해 주세요.");
    if (!birthDate) return alert("생년월일을 입력해 주세요.");
    if (!gender) return alert("성별을 선택해 주세요.");
    if (!countryName) return alert("국가를 입력해 주세요.");
    if (!countryCode) {
      alert("지원하지 않는 국가입니다.\n예: 대한민국, Korea, Japan, USA");
      return;
    }
    if (!region) return alert("지역을 입력해 주세요.");
    if (!role) return alert("회원 유형을 선택해 주세요.");

    if (!isEmailVerified) {
      alert("회원가입 전에 이메일 인증을 완료해 주세요.");
      return;
    }

    const payload = {
      email,
      password,
      name,
      role,
      birthDate,
      gender,
      countryCode,
      region,
    };

    try {
      const response = await fetch("/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const contentType = response.headers.get("content-type") || "";
        let errorMessage = "회원가입에 실패했습니다.";

        if (contentType.includes("application/json")) {
          const errorData = await response.json().catch(() => null);
          errorMessage = errorData?.message || errorData?.error || errorMessage;
        } else {
          const text = await response.text().catch(() => "");
          if (text) errorMessage = text;
        }

        throw new Error(errorMessage);
      }

      alert("회원가입이 완료되었습니다.");
      window.location.href = "/auth/login";
    } catch (error) {
      alert(error?.message || "회원가입에 실패했습니다.");
    }
  });
});

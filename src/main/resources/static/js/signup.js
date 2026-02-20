/* ===============================
   auth.js (로그인 + 회원가입 + i18n 통합)
================================ */

/* ===== 언어 목록 ===== */
const AVAILABLE_LANGUAGES = [
  { code: "ko", label: "한국어" },
  { code: "ja", label: "日本語" },
  { code: "en", label: "English" }
];

/* ===== 번역 사전 ===== */
const I18N = {
  ko: {
    // login UI
    "login.title": "로그인",
    "login.subtitle": "다시 만나서 반가워요. 로그인해주세요.",
    "login.emailLabel": "이메일 주소",
    "login.emailPlaceholder": "email@example.com",
    "login.passwordLabel": "비밀번호",
    "login.passwordPlaceholder": "비밀번호",
    "login.submit": "로그인",
    "login.forgot": "비밀번호를 잊으셨나요?",
    "login.signup": "회원가입",

    // login alerts
    "login.alert.emailRequired": "이메일을 입력해주세요.",
    "login.alert.passwordRequired": "비밀번호를 입력해주세요.",
    "login.alert.failed": "로그인에 실패했습니다.",

    // signup UI
    "signup.title": "회원가입",
    "signup.subtitle": "계정을 생성해주세요",
    "signup.emailLabel": "이메일 주소",
    "signup.sendCode": "인증 코드 보내기",
    "signup.codePlaceholder": "6-digit code",
    "signup.verifyCode": "인증하기",
    "signup.passwordLabel": "비밀번호",
    "signup.passwordConfirmLabel": "비밀번호 확인",
    "signup.nicknameLabel": "닉네임",
    "signup.birthDateLabel": "생년월일",
    "signup.genderLabel": "성별",
    "signup.gender.female": "여성",
    "signup.gender.male": "남성",
    "signup.gender.other": "기타",
    "signup.countryLabel": "국가",
    "signup.countryPlaceholder": "예: 대한민국, Korea, Japan",
    "signup.regionLabel": "지역",
    "signup.regionPlaceholder": "예: 부산, 도쿄, 서울",
    "signup.roleLabel": "회원 유형",
    "signup.role.user": "여행자",
    "signup.role.host": "가이드",
    "signup.submit": "회원가입",
    "signup.haveAccount": "이미 계정이 있으신가요?",
    "signup.loginLink": "로그인",

    // signup status/alerts
    "signup.status.verified": "✅ 이메일 인증이 완료되었습니다.",
    "signup.alert.emailRequired": "이메일 주소를 입력해 주세요.",
    "signup.alert.codeRequired": "6자리 인증 코드를 입력해 주세요.",
    "signup.alert.codeInvalid": "인증 코드는 6자리 숫자여야 합니다.",
    "signup.alert.passwordRequired": "비밀번호를 입력해 주세요.",
    "signup.alert.passwordConfirmRequired": "비밀번호 확인을 입력해 주세요.",
    "signup.alert.passwordMismatch": "비밀번호가 일치하지 않습니다.",
    "signup.alert.nicknameRequired": "닉네임을 입력해 주세요.",
    "signup.alert.birthDateRequired": "생년월일을 입력해 주세요.",
    "signup.alert.genderRequired": "성별을 선택해 주세요.",
    "signup.alert.countryRequired": "국가를 입력해 주세요.",
    "signup.alert.countryUnsupported": "지원하지 않는 국가입니다.\n예: 대한민국, Korea, Japan, USA",
    "signup.alert.regionRequired": "지역을 입력해 주세요.",
    "signup.alert.roleRequired": "회원 유형을 선택해 주세요.",
    "signup.alert.emailNotVerified": "회원가입 전에 이메일 인증을 완료해 주세요.",
    "signup.alert.failed": "회원가입에 실패했습니다.",
    "signup.alert.success": "회원가입이 완료되었습니다.",

    // modal/common
    "common.confirm": "확인",
    "signup.modal.sendGuideTitle": "인증 메일 전송 안내",
    "signup.modal.sendGuideMsg": "인증 메일을 전송했습니다. 메일 도착까지 몇 분 정도 소요될 수 있으며, 스팸함도 함께 확인해 주세요.",
    "signup.modal.sentTitle": "인증 메일이 전송되었습니다.",
    "signup.modal.sentMsg": "전송이 완료될 때까지 시간이 소요될 수 있습니다.",
    "signup.modal.sendFailTitle": "전송 실패",
    "signup.modal.sendFailMsg": "인증 코드 전송에 실패했습니다. 잠시 후 다시 시도해 주세요.",
    "signup.status.codeSent": "인증 코드가 전송되었습니다. 이메일을 확인해 주세요."
  },

  ja: {
    // login UI
    "login.title": "ログイン",
    "login.subtitle": "また会えて嬉しいです。ログインしてください。",
    "login.emailLabel": "メールアドレス",
    "login.emailPlaceholder": "email@example.com",
    "login.passwordLabel": "パスワード",
    "login.passwordPlaceholder": "パスワード",
    "login.submit": "ログイン",
    "login.forgot": "パスワードをお忘れですか？",
    "login.signup": "会員登録",

    // login alerts
    "login.alert.emailRequired": "メールアドレスを入力してください。",
    "login.alert.passwordRequired": "パスワードを入力してください。",
    "login.alert.failed": "ログインに失敗しました。",

    // signup UI
    "signup.title": "会員登録",
    "signup.subtitle": "アカウントを作成してください",
    "signup.emailLabel": "メールアドレス",
    "signup.sendCode": "認証コードを送信",
    "signup.codePlaceholder": "6桁コード",
    "signup.verifyCode": "認証する",
    "signup.passwordLabel": "パスワード",
    "signup.passwordConfirmLabel": "パスワード（確認）",
    "signup.nicknameLabel": "ニックネーム",
    "signup.birthDateLabel": "生年月日",
    "signup.genderLabel": "性別",
    "signup.gender.female": "女性",
    "signup.gender.male": "男性",
    "signup.gender.other": "その他",
    "signup.countryLabel": "国",
    "signup.countryPlaceholder": "例：韓国、Korea、Japan",
    "signup.regionLabel": "地域",
    "signup.regionPlaceholder": "例：釜山、東京、ソウル",
    "signup.roleLabel": "会員タイプ",
    "signup.role.user": "旅行者",
    "signup.role.host": "ガイド",
    "signup.submit": "会員登録",
    "signup.haveAccount": "すでにアカウントをお持ちですか？",
    "signup.loginLink": "ログイン",

    // signup status/alerts
    "signup.status.verified": "✅ メール認証が完了しました。",
    "signup.alert.emailRequired": "メールアドレスを入力してください。",
    "signup.alert.codeRequired": "6桁の認証コードを入力してください。",
    "signup.alert.codeInvalid": "認証コードは6桁の数字である必要があります。",
    "signup.alert.passwordRequired": "パスワードを入力してください。",
    "signup.alert.passwordConfirmRequired": "パスワード（確認）を入力してください。",
    "signup.alert.passwordMismatch": "パスワードが一致しません。",
    "signup.alert.nicknameRequired": "ニックネームを入力してください。",
    "signup.alert.birthDateRequired": "生年月日を入力してください。",
    "signup.alert.genderRequired": "性別を選択してください。",
    "signup.alert.countryRequired": "国を入力してください。",
    "signup.alert.countryUnsupported": "対応していない国です。\n例：韓国、Korea、Japan、USA",
    "signup.alert.regionRequired": "地域を入力してください。",
    "signup.alert.roleRequired": "会員タイプを選択してください。",
    "signup.alert.emailNotVerified": "登録前にメール認証を完了してください。",
    "signup.alert.failed": "会員登録に失敗しました。",
    "signup.alert.success": "会員登録が完了しました。",

    // modal/common
    "common.confirm": "確認",
    "signup.modal.sendGuideTitle": "認証メール送信のご案内",
    "signup.modal.sendGuideMsg": "認証メールを送信しました。到着まで数分かかる場合があります。迷惑メールフォルダも確認してください。",
    "signup.modal.sentTitle": "認証メールを送信しました。",
    "signup.modal.sentMsg": "反映まで少し時間がかかる場合があります。",
    "signup.modal.sendFailTitle": "送信失敗",
    "signup.modal.sendFailMsg": "認証コードの送信に失敗しました。しばらくしてからもう一度お試しください。",
    "signup.status.codeSent": "認証コードを送信しました。メールを確認してください。"
  },

  en: {
    // login UI
    "login.title": "Sign In",
    "login.subtitle": "Welcome back. Please sign in.",
    "login.emailLabel": "Email",
    "login.emailPlaceholder": "email@example.com",
    "login.passwordLabel": "Password",
    "login.passwordPlaceholder": "Password",
    "login.submit": "Sign In",
    "login.forgot": "Forgot your password?",
    "login.signup": "Sign Up",

    // login alerts
    "login.alert.emailRequired": "Please enter your email address.",
    "login.alert.passwordRequired": "Please enter your password.",
    "login.alert.failed": "Sign in failed.",

    // signup UI
    "signup.title": "Sign Up",
    "signup.subtitle": "Create your account",
    "signup.emailLabel": "Email",
    "signup.sendCode": "Send verification code",
    "signup.codePlaceholder": "6-digit code",
    "signup.verifyCode": "Verify",
    "signup.passwordLabel": "Password",
    "signup.passwordConfirmLabel": "Confirm password",
    "signup.nicknameLabel": "Nickname",
    "signup.birthDateLabel": "Birth date",
    "signup.genderLabel": "Gender",
    "signup.gender.female": "Female",
    "signup.gender.male": "Male",
    "signup.gender.other": "Other",
    "signup.countryLabel": "Country",
    "signup.countryPlaceholder": "e.g., Korea, Japan, USA",
    "signup.regionLabel": "Region",
    "signup.regionPlaceholder": "e.g., Busan, Tokyo, Seoul",
    "signup.roleLabel": "Member type",
    "signup.role.user": "Traveler",
    "signup.role.host": "Guide",
    "signup.submit": "Sign Up",
    "signup.haveAccount": "Already have an account?",
    "signup.loginLink": "Sign In",

    // signup status/alerts
    "signup.status.verified": "✅ Email verification completed.",
    "signup.alert.emailRequired": "Please enter your email address.",
    "signup.alert.codeRequired": "Please enter the 6-digit code.",
    "signup.alert.codeInvalid": "The code must be a 6-digit number.",
    "signup.alert.passwordRequired": "Please enter your password.",
    "signup.alert.passwordConfirmRequired": "Please confirm your password.",
    "signup.alert.passwordMismatch": "Passwords do not match.",
    "signup.alert.nicknameRequired": "Please enter your nickname.",
    "signup.alert.birthDateRequired": "Please enter your birth date.",
    "signup.alert.genderRequired": "Please select your gender.",
    "signup.alert.countryRequired": "Please enter your country.",
    "signup.alert.countryUnsupported": "Unsupported country.\nExample: Korea, Japan, USA",
    "signup.alert.regionRequired": "Please enter your region.",
    "signup.alert.roleRequired": "Please select a member type.",
    "signup.alert.emailNotVerified": "Please complete email verification before signing up.",
    "signup.alert.failed": "Sign up failed.",
    "signup.alert.success": "Sign up completed.",

    // modal/common
    "common.confirm": "OK",
    "signup.modal.sendGuideTitle": "Verification email sent",
    "signup.modal.sendGuideMsg": "We sent a verification email. It may take a few minutes. Please also check your spam folder.",
    "signup.modal.sentTitle": "Email sent.",
    "signup.modal.sentMsg": "It may take a moment to be delivered.",
    "signup.modal.sendFailTitle": "Send failed",
    "signup.modal.sendFailMsg": "Failed to send the verification code. Please try again later.",
    "signup.status.codeSent": "Code sent. Please check your email."
  }
};

/* ===== i18n util ===== */
function getCurrentLang() {
  return localStorage.getItem("lang") || "ko";
}

function t(key) {
  const lang = getCurrentLang();
  return I18N[lang]?.[key] ?? I18N.ko[key] ?? key;
}

function applyLanguage(lang) {
  document.querySelectorAll("[data-i18n]").forEach(el => {
    el.innerText = t(el.dataset.i18n);
  });
  document.querySelectorAll("[data-i18n-placeholder]").forEach(el => {
    el.placeholder = t(el.dataset.i18nPlaceholder);
  });
  document.documentElement.lang = lang;
}

function initLanguageSelect() {
  const select = document.getElementById("langSelect");
  if (!select) return;

  select.innerHTML = "";
  AVAILABLE_LANGUAGES.forEach(lang => {
    const option = document.createElement("option");
    option.value = lang.code;
    option.innerText = lang.label;
    select.appendChild(option);
  });

  const saved = getCurrentLang();
  select.value = saved;
  applyLanguage(saved);

  select.addEventListener("change", () => {
    localStorage.setItem("lang", select.value);
    applyLanguage(select.value);
  });
}

/* ============================
   국가 텍스트 -> 코드 변환
============================ */
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

    // CN
    "china": "CN",
    "중국": "CN",
  };

  return map[n] || null;
}

/* ============================
   로그인 init
============================ */
function initLoginForm() {
  const form = document.getElementById("loginForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email")?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";

    if (!email) return alert(t("login.alert.emailRequired"));
    if (!password) return alert(t("login.alert.passwordRequired"));

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
        throw new Error(data?.message || `${t("login.alert.failed")} (${res.status})`);
      }

      sessionStorage.setItem("justLoggedIn", "1");
      window.location.href = "/";
    } catch (err) {
      alert(err?.message || t("login.alert.failed"));
    }
  });
}

/* ============================
   회원가입 init
============================ */
function initSignupForm() {
  const form = document.getElementById("signupForm");
  if (!form) return;

  const emailInput = document.getElementById("email");
  const sendCodeBtn = document.getElementById("sendCodeBtn");
  const verifyCodeBtn = document.getElementById("verifyCodeBtn");
  const codeInput = document.getElementById("verificationCode");
  const statusEl = document.getElementById("emailVerifyStatus");
  const signupBtn = document.getElementById("signupBtn");

  const birthDateInput = document.getElementById("birthDate");
  if (birthDateInput) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    birthDateInput.max = `${yyyy}-${mm}-${dd}`;
  }
  const countryInput = document.getElementById("country");
  const regionInput = document.getElementById("region");

  // modal
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
      setStatus(t("signup.status.verified"));
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

  // 인증 코드 전송
  if (sendCodeBtn) {
    sendCodeBtn.addEventListener("click", async () => {
      const email = emailInput?.value?.trim();
      if (!email) return alert(t("signup.alert.emailRequired"));

      openMailModal(t("signup.modal.sendGuideTitle"), t("signup.modal.sendGuideMsg"));

      try {
        const res = await fetch("/api/auth/email/send-code", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email }),
        });

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || t("signup.modal.sendFailMsg"));
        }

        setStatus(t("signup.status.codeSent"));
      } catch (err) {
        openMailModal(
          t("signup.modal.sendFailTitle"),
          err?.message || t("signup.modal.sendFailMsg")
        );
      }
    });
  }

  // 인증 코드 확인
  if (verifyCodeBtn) {
    verifyCodeBtn.addEventListener("click", async () => {
      const email = emailInput?.value?.trim();
      const code = codeInput?.value?.trim();

      if (!email) return alert(t("signup.alert.emailRequired"));
      if (!code) return alert(t("signup.alert.codeRequired"));
      if (!/^\d{6}$/.test(code)) return alert(t("signup.alert.codeInvalid"));

      try {
        const res = await fetch("/api/auth/email/verify-code", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, code }),
        });

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || t("signup.alert.codeInvalid"));
        }

        setVerifiedState(true);
        alert(t("signup.status.verified"));
      } catch (err) {
        alert(err?.message || t("signup.alert.codeInvalid"));
      }
    });
  }

  // 회원가입 제출
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = emailInput?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";
    const passwordConfirm = document.getElementById("passwordConfirm")?.value ?? "";
    const name = document.getElementById("name")?.value?.trim();

    const role = document.querySelector('input[name="role"]:checked')?.value ?? null;
    const birthDate = birthDateInput?.value || "";
    const gender = document.querySelector('input[name="gender"]:checked')?.value ?? null;

    const countryName = countryInput?.value?.trim() || "";
    const countryCode = countryNameToCode(countryName);

    const region = regionInput?.value?.trim() || "";

    if (!email) return alert(t("signup.alert.emailRequired"));
    if (!password) return alert(t("signup.alert.passwordRequired"));
    if (!passwordConfirm) return alert(t("signup.alert.passwordConfirmRequired"));
    if (password !== passwordConfirm) return alert(t("signup.alert.passwordMismatch"));
    if (!name) return alert(t("signup.alert.nicknameRequired"));
    if (!birthDate) return alert(t("signup.alert.birthDateRequired"));
    if (!gender) return alert(t("signup.alert.genderRequired"));
    if (!countryName) return alert(t("signup.alert.countryRequired"));
    if (!countryCode) return alert(t("signup.alert.countryUnsupported"));
    if (!region) return alert(t("signup.alert.regionRequired"));
    if (!role) return alert(t("signup.alert.roleRequired"));

    if (!isEmailVerified) return alert(t("signup.alert.emailNotVerified"));

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
        let errorMessage = t("signup.alert.failed");

        if (contentType.includes("application/json")) {
          const errorData = await response.json().catch(() => null);
          errorMessage = errorData?.message || errorData?.error || errorMessage;
        } else {
          const text = await response.text().catch(() => "");
          if (text) errorMessage = text;
        }

        throw new Error(errorMessage);
      }

      alert(t("signup.alert.success"));
      window.location.href = "/auth/login";
    } catch (error) {
      alert(error?.message || t("signup.alert.failed"));
    }
  });
}

/* ===== init ===== */
document.addEventListener("DOMContentLoaded", () => {
  initLanguageSelect();
  initLoginForm();   // login 페이지에만 있으면 동작
  initSignupForm();  // signup 페이지에만 있으면 동작
});

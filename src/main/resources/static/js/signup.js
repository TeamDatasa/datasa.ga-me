// ============================
// Language
// ============================
const AVAILABLE_LANGUAGES = [
  { code: "ko", label: "한국어" },
  { code: "ja", label: "日本語" },
  { code: "en", label: "English" }
];

const I18N = {
  ko: {
    "login.title": "로그인",
    "login.subtitle": "다시 만나서 반가워요. 로그인해주세요.",
    "login.emailLabel": "이메일 주소",
    "login.emailPlaceholder": "email@example.com",
    "login.passwordLabel": "비밀번호",
    "login.passwordPlaceholder": "비밀번호",
    "login.submit": "로그인",
    "login.forgot": "비밀번호를 잊으셨나요?",
    "login.signup": "회원가입",
    "login.alert.emailRequired": "이메일을 입력해주세요.",
    "login.alert.passwordRequired": "비밀번호를 입력해주세요.",
    "login.alert.failed": "로그인에 실패했습니다.",

    "signup.title": "회원가입",
    "signup.subtitle": "계정을 생성해주세요",
    "signup.emailLabel": "이메일 주소",
    "signup.alert.emailExists": "이미 가입된 이메일입니다.",
    "signup.sendCode": "인증 코드 보내기",
    "signup.codePlaceholder": "6-digit code",
    "signup.verifyCode": "인증하기",
    "signup.alert.codeWrong": "인증코드를 다시 입력해 주세요.",
    "signup.passwordLabel": "비밀번호",
    "signup.passwordConfirmLabel": "비밀번호 확인",
    "signup.nicknameLabel": "닉네임",
    "signup.birthDateLabel": "생년월일",
    "signup.genderLabel": "성별",
    "signup.gender.female": "여성",
    "signup.gender.male": "남성",
    "signup.gender.other": "기타",
    "signup.countryLabel": "국가",
    "signup.countrySelectPlaceholder": "국가 선택",
    "signup.regionLabel": "지역",
    "signup.regionSelectPlaceholder": "지역 선택",
    "signup.regionPlaceholder": "예: 부산, 도쿄, 서울",
    "signup.submit": "회원가입",
    "signup.haveAccount": "이미 계정이 있으신가요?",
    "signup.loginLink": "로그인",

    "signup.status.verified": "이메일 인증이 완료되었습니다.",
    "signup.alert.emailRequired": "이메일 주소를 입력해 주세요.",
    "signup.alert.codeRequired": "6자리 인증 코드를 입력해 주세요.",
    "signup.alert.codeInvalid": "인증 코드는 6자리 숫자여야 합니다.",
    "signup.alert.passwordRequired": "비밀번호를 입력해 주세요.",
    "signup.alert.passwordConfirmRequired": "비밀번호 확인을 입력해 주세요.",
    "signup.alert.passwordMismatch": "비밀번호가 일치하지 않습니다.",
    "signup.alert.nicknameRequired": "닉네임을 입력해 주세요.",
    "signup.alert.birthDateRequired": "생년월일을 입력해 주세요.",
    "signup.alert.genderRequired": "성별을 선택해 주세요.",
    "signup.alert.countryRequired": "국가를 선택해 주세요.",
    "signup.alert.regionRequired": "지역을 입력/선택해 주세요.",
    "signup.alert.emailNotVerified": "회원가입 전에 이메일 인증을 완료해 주세요.",
    "signup.alert.failed": "회원가입에 실패했습니다.",
    "signup.alert.success": "회원가입이 완료되었습니다.",

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
    "login.title": "ログイン",
    "login.subtitle": "また会えて嬉しいです。ログインしてください。",
    "login.emailLabel": "メールアドレス",
    "login.emailPlaceholder": "email@example.com",
    "login.passwordLabel": "パスワード",
    "login.passwordPlaceholder": "パスワード",
    "login.submit": "ログイン",
    "login.forgot": "パスワードをお忘れですか？",
    "login.signup": "会員登録",
    "login.alert.emailRequired": "メールアドレスを入力してください。",
    "login.alert.passwordRequired": "パスワードを入力してください。",
    "login.alert.failed": "ログインに失敗しました。",

    "signup.title": "会員登録",
    "signup.subtitle": "アカウントを作成してください",
    "signup.emailLabel": "メールアドレス",
    "signup.alert.emailExists": "すでに登録されているメールアドレスです。",
    "signup.sendCode": "認証コードを送信",
    "signup.codePlaceholder": "6桁コード",
    "signup.verifyCode": "認証する",
    "signup.alert.codeWrong": "認証コードをもう一度入力してください。",
    "signup.passwordLabel": "パスワード",
    "signup.passwordConfirmLabel": "パスワード（確認）",
    "signup.nicknameLabel": "ニックネーム",
    "signup.birthDateLabel": "生年月日",
    "signup.genderLabel": "性別",
    "signup.gender.female": "女性",
    "signup.gender.male": "男性",
    "signup.gender.other": "その他",
    "signup.countryLabel": "国",
    "signup.countrySelectPlaceholder": "国を選択",
    "signup.regionLabel": "地域",
    "signup.regionSelectPlaceholder": "地域を選択",
    "signup.regionPlaceholder": "例：釜山、東京、ソウル",
    "signup.submit": "会員登録",
    "signup.haveAccount": "すでにアカウントをお持ちですか？",
    "signup.loginLink": "ログイン",

    "signup.status.verified": "メール認証が完了しました。",
    "signup.alert.emailRequired": "メールアドレスを入力してください。",
    "signup.alert.codeRequired": "6桁の認証コードを入力してください。",
    "signup.alert.codeInvalid": "認証コードは6桁の数字である必要があります。",
    "signup.alert.passwordRequired": "パスワードを入力してください。",
    "signup.alert.passwordConfirmRequired": "パスワード（確認）を入力してください。",
    "signup.alert.passwordMismatch": "パスワードが一致しません。",
    "signup.alert.nicknameRequired": "ニックネームを入力してください。",
    "signup.alert.birthDateRequired": "生年月日を入力してください。",
    "signup.alert.genderRequired": "性別を選択してください。",
    "signup.alert.countryRequired": "国を選択してください。",
    "signup.alert.regionRequired": "地域を入力/選択してください。",
    "signup.alert.emailNotVerified": "登録前にメール認証を完了してください。",
    "signup.alert.failed": "会員登録に失敗しました。",
    "signup.alert.success": "会員登録が完了しました。",

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
    "login.title": "Sign In",
    "login.subtitle": "Welcome back. Please sign in.",
    "login.emailLabel": "Email",
    "login.emailPlaceholder": "email@example.com",
    "login.passwordLabel": "Password",
    "login.passwordPlaceholder": "Password",
    "login.submit": "Sign In",
    "login.forgot": "Forgot your password?",
    "login.signup": "Sign Up",
    "login.alert.emailRequired": "Please enter your email address.",
    "login.alert.passwordRequired": "Please enter your password.",
    "login.alert.failed": "Sign in failed.",

    "signup.title": "Sign Up",
    "signup.subtitle": "Create your account",
    "signup.emailLabel": "Email",
    "signup.alert.emailExists": "This email is already registered.",
    "signup.sendCode": "Send verification code",
    "signup.codePlaceholder": "6-digit code",
    "signup.verifyCode": "Verify",
    "signup.alert.codeWrong": "Please re-enter the verification code.",
    "signup.passwordLabel": "Password",
    "signup.passwordConfirmLabel": "Confirm password",
    "signup.nicknameLabel": "Nickname",
    "signup.birthDateLabel": "Birth date",
    "signup.genderLabel": "Gender",
    "signup.gender.female": "Female",
    "signup.gender.male": "Male",
    "signup.gender.other": "Other",
    "signup.countryLabel": "Country",
    "signup.countrySelectPlaceholder": "Select a country",
    "signup.regionLabel": "Region",
    "signup.regionSelectPlaceholder": "Select a region",
    "signup.regionPlaceholder": "e.g., Busan, Tokyo, Seoul",
    "signup.submit": "Sign Up",
    "signup.haveAccount": "Already have an account?",
    "signup.loginLink": "Sign In",

    "signup.status.verified": "Email verification completed.",
    "signup.alert.emailRequired": "Please enter your email address.",
    "signup.alert.codeRequired": "Please enter the 6-digit code.",
    "signup.alert.codeInvalid": "The code must be a 6-digit number.",
    "signup.alert.passwordRequired": "Please enter your password.",
    "signup.alert.passwordConfirmRequired": "Please confirm your password.",
    "signup.alert.passwordMismatch": "Passwords do not match.",
    "signup.alert.nicknameRequired": "Please enter your nickname.",
    "signup.alert.birthDateRequired": "Please enter your birth date.",
    "signup.alert.genderRequired": "Please select your gender.",
    "signup.alert.countryRequired": "Please select your country.",
    "signup.alert.regionRequired": "Please enter/select your region.",
    "signup.alert.emailNotVerified": "Please complete email verification before signing up.",
    "signup.alert.failed": "Sign up failed.",
    "signup.alert.success": "Sign up completed.",

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

function getCurrentLang() {
  return localStorage.getItem("lang") || "ko";
}
function t(key) {
  const lang = getCurrentLang();
  return I18N[lang]?.[key] ?? I18N.ko[key] ?? key;
}

// ============================
// Country/Region (language-aware)
// ============================
const COUNTRIES_BY_LANG = {
  ko: [
    { code: "KR", label: "대한민국" },
    { code: "JP", label: "일본" },
    { code: "US", label: "미국" },
    { code: "CN", label: "중국" },
    { code: "TW", label: "대만" },
    { code: "HK", label: "홍콩" },
    { code: "SG", label: "싱가포르" },
    { code: "TH", label: "태국" },
    { code: "VN", label: "베트남" },
    { code: "PH", label: "필리핀" },
    { code: "MY", label: "말레이시아" },
    { code: "ID", label: "인도네시아" },
    { code: "AU", label: "호주" },
    { code: "CA", label: "캐나다" },
    { code: "GB", label: "영국" },
    { code: "FR", label: "프랑스" },
    { code: "DE", label: "독일" },
    { code: "ES", label: "스페인" },
    { code: "IT", label: "이탈리아" }
  ],
  ja: [
    { code: "KR", label: "韓国" },
    { code: "JP", label: "日本" },
    { code: "US", label: "アメリカ" },
    { code: "CN", label: "中国" },
    { code: "TW", label: "台湾" },
    { code: "HK", label: "香港" },
    { code: "SG", label: "シンガポール" },
    { code: "TH", label: "タイ" },
    { code: "VN", label: "ベトナム" },
    { code: "PH", label: "フィリピン" },
    { code: "MY", label: "マレーシア" },
    { code: "ID", label: "インドネシア" },
    { code: "AU", label: "オーストラリア" },
    { code: "CA", label: "カナダ" },
    { code: "GB", label: "イギリス" },
    { code: "FR", label: "フランス" },
    { code: "DE", label: "ドイツ" },
    { code: "ES", label: "スペイン" },
    { code: "IT", label: "イタリア" }
  ],
  en: [
    { code: "KR", label: "South Korea" },
    { code: "JP", label: "Japan" },
    { code: "US", label: "United States" },
    { code: "CN", label: "China" },
    { code: "TW", label: "Taiwan" },
    { code: "HK", label: "Hong Kong" },
    { code: "SG", label: "Singapore" },
    { code: "TH", label: "Thailand" },
    { code: "VN", label: "Vietnam" },
    { code: "PH", label: "Philippines" },
    { code: "MY", label: "Malaysia" },
    { code: "ID", label: "Indonesia" },
    { code: "AU", label: "Australia" },
    { code: "CA", label: "Canada" },
    { code: "GB", label: "United Kingdom" },
    { code: "FR", label: "France" },
    { code: "DE", label: "Germany" },
    { code: "ES", label: "Spain" },
    { code: "IT", label: "Italy" }
  ]
};

const REGIONS_BY_LANG = {
  ko: {
    KR: ["서울","부산","대구","인천","광주","대전","울산","세종","경기","강원","충북","충남","전북","전남","경북","경남","제주"],
    JP: ["도쿄","오사카","교토","가나가와","사이타마","치바","아이치","후쿠오카","홋카이도","오키나와","히로시마","미야기","시즈오카"]
  },
  ja: {
    KR: ["ソウル","釜山","大邱","仁川","光州","大田","蔚山","世宗","京畿","江原","忠北","忠南","全北","全南","慶北","慶南","済州"],
    JP: ["東京都","大阪府","京都府","神奈川県","埼玉県","千葉県","愛知県","福岡県","北海道","沖縄県","広島県","宮城県","静岡県"]
  },
  en: {
    KR: ["Seoul","Busan","Daegu","Incheon","Gwangju","Daejeon","Ulsan","Sejong","Gyeonggi","Gangwon","Chungbuk","Chungnam","Jeonbuk","Jeonnam","Gyeongbuk","Gyeongnam","Jeju"],
    JP: ["Tokyo","Osaka","Kyoto","Kanagawa","Saitama","Chiba","Aichi","Fukuoka","Hokkaido","Okinawa","Hiroshima","Miyagi","Shizuoka"]
  }
};

function initCountrySelect() {
  const sel = document.getElementById("country");
  if (!sel) return;

  const lang = getCurrentLang();
  const list = COUNTRIES_BY_LANG[lang] || COUNTRIES_BY_LANG.ko;

  const current = sel.value;
  sel.innerHTML =
    `<option value="">${t("signup.countrySelectPlaceholder")}</option>` +
    list.map(c => `<option value="${c.code}">${c.label}</option>`).join("");

  if (current) sel.value = current;
}

const REGION_PLACEHOLDER_BY_LANG_AND_COUNTRY = {
  ko: {
    US: "예: 캘리포니아, 뉴욕, 텍사스",
    default: "예: 부산, 도쿄, 서울"
  },
  ja: {
    US: "例：カリフォルニア、ニューヨーク、テキサス",
    default: "例：釜山、東京、ソウル"
  },
  en: {
    US: "e.g., California, New York, Texas",
    default: "e.g., Busan, Tokyo, Seoul"
  }
};

function setRegionInputPlaceholder(countryCode) {
  const regionInput = document.getElementById("regionInput");
  if (!regionInput) return;

  const lang = getCurrentLang();
  const map = REGION_PLACEHOLDER_BY_LANG_AND_COUNTRY[lang] || REGION_PLACEHOLDER_BY_LANG_AND_COUNTRY.ko;

  regionInput.placeholder = map[countryCode] || map.default;
}

function updateRegionUI() {
  const country = document.getElementById("country")?.value || "";
  const regionSelect = document.getElementById("regionSelect");
  const regionInput = document.getElementById("regionInput");
  if (!regionSelect || !regionInput) return;

  const lang = getCurrentLang();
  const map = REGIONS_BY_LANG[lang] || REGIONS_BY_LANG.ko;

  if (country === "KR" || country === "JP") {
    const list = map[country] || [];
    const current = regionSelect.value;

    regionSelect.innerHTML =
      `<option value="">${t("signup.regionSelectPlaceholder")}</option>` +
      list.map(r => `<option value="${r}">${r}</option>`).join("");

    regionSelect.style.display = "";
    regionSelect.required = true;

    regionInput.style.display = "none";
    regionInput.required = false;
    regionInput.value = "";

    if (current) regionSelect.value = current;
  } else if (country) {
    regionSelect.style.display = "none";
    regionSelect.required = false;
    regionSelect.innerHTML = "";

    regionInput.style.display = "";
    regionInput.required = true;

    setRegionInputPlaceholder(country);
  } else {
    regionSelect.style.display = "none";
    regionSelect.required = false;
    regionSelect.innerHTML = "";

    regionInput.style.display = "none";
    regionInput.required = false;
    regionInput.value = "";

    setRegionInputPlaceholder("");
  }
}

// ============================
// i18n apply
// ============================
function applyLanguage(lang) {
const birthDateInput = document.getElementById("birthDate");
if (birthDateInput) birthDateInput.lang = lang;
  document.querySelectorAll("[data-i18n]").forEach(el => {
    el.innerText = t(el.dataset.i18n);
  });
  document.querySelectorAll("[data-i18n-placeholder]").forEach(el => {
    el.placeholder = t(el.dataset.i18nPlaceholder);
  });
  document.documentElement.lang = lang;

  // 언어 변경 시 국가/지역 옵션도 즉시 번역 반영
  initCountrySelect();
  updateRegionUI();
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

// ============================
// Login (optional on signup page)
// ============================
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
      const data = raw && contentType.includes("application/json") ? JSON.parse(raw) : null;

      if (!res.ok) throw new Error(data?.message || `${t("login.alert.failed")} (${res.status})`);

      sessionStorage.setItem("justLoggedIn", "1");
      window.location.href = "/";
    } catch (err) {
      alert(err?.message || t("login.alert.failed"));
    }
  });
}

// ============================
// Signup
// ============================
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
    // ✅ 만 18세 이상만 선택 가능: max = 오늘 - 18년
    const today = new Date();
    const max = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
    const yyyy = max.getFullYear();
    const mm = String(max.getMonth() + 1).padStart(2, "0");
    const dd = String(max.getDate()).padStart(2, "0");
    birthDateInput.max = `${yyyy}-${mm}-${dd}`;
  }

  // country/region init
  initCountrySelect();
  document.getElementById("country")?.addEventListener("change", updateRegionUI);
  updateRegionUI();

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

  mailModalClose?.addEventListener("click", closeMailModal);

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

  // 이메일 변경 시 인증 리셋
  emailInput?.addEventListener("input", () => {
    isEmailVerified = false;
    if (signupBtn) signupBtn.disabled = true;
    if (sendCodeBtn) sendCodeBtn.disabled = false;
    if (verifyCodeBtn) verifyCodeBtn.disabled = false;
    if (emailInput) emailInput.readOnly = false;
    if (codeInput) codeInput.readOnly = false;
    setStatus("");
  });

  // ✅ 인증 코드 전송 (성공 시에만 “전송 안내” 모달)
  sendCodeBtn?.addEventListener("click", async () => {
    const email = emailInput?.value?.trim();
    if (!email) return alert(t("signup.alert.emailRequired"));

    try {
      const res = await fetch("/api/auth/email/send-code", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });

      if (!res.ok) {
        const ct = res.headers.get("content-type") || "";
        let msg = t("signup.modal.sendFailMsg");

        if (ct.includes("application/json")) {
          const err = await res.json().catch(() => null);
          msg = err?.message || err?.error || msg;
        } else {
          const text = await res.text().catch(() => "");
          if (text) msg = text;
        }

        if (res.status === 409) msg = t("signup.alert.emailExists");

        openMailModal(t("signup.modal.sendFailTitle"), msg);
        return;
      }

      openMailModal(t("signup.modal.sendGuideTitle"), t("signup.modal.sendGuideMsg"));
      setStatus(t("signup.status.codeSent"));
    } catch (err) {
      openMailModal(
        t("signup.modal.sendFailTitle"),
        err?.message || t("signup.modal.sendFailMsg")
      );
    }
  });

  // ✅ 인증 코드 확인
  verifyCodeBtn?.addEventListener("click", async () => {
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

      if (!res.ok) throw new Error(t("signup.alert.codeWrong"));

      setVerifiedState(true);
      alert(t("signup.status.verified"));
    } catch (err) {
      alert(err?.message || t("signup.alert.codeWrong"));
    }
  });

  // ✅ 회원가입 제출 (role 제거됨)
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = emailInput?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";
    const passwordConfirm = document.getElementById("passwordConfirm")?.value ?? "";
    const name = document.getElementById("name")?.value?.trim();

    const birthDate = birthDateInput?.value || "";
    const gender = document.querySelector('input[name="gender"]:checked')?.value ?? null;

    const countryCode = document.getElementById("country")?.value || "";

    const regionSelect = document.getElementById("regionSelect");
    const regionInput = document.getElementById("regionInput");
    const region =
      (regionSelect && regionSelect.style.display !== "none"
        ? regionSelect.value
        : regionInput?.value
      )?.trim() || "";

    if (!email) return alert(t("signup.alert.emailRequired"));
    if (!password) return alert(t("signup.alert.passwordRequired"));
    if (!passwordConfirm) return alert(t("signup.alert.passwordConfirmRequired"));
    if (password !== passwordConfirm) return alert(t("signup.alert.passwordMismatch"));
    if (!name) return alert(t("signup.alert.nicknameRequired"));
    if (!birthDate) return alert(t("signup.alert.birthDateRequired"));
    if (!gender) return alert(t("signup.alert.genderRequired"));
    if (!countryCode) return alert(t("signup.alert.countryRequired"));
    if (!region) return alert(t("signup.alert.regionRequired"));
    if (!isEmailVerified) return alert(t("signup.alert.emailNotVerified"));

    const payload = {
      email,
      password,
      name,
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

document.addEventListener("DOMContentLoaded", () => {
  initLanguageSelect();
  initLoginForm();
  initSignupForm();
});
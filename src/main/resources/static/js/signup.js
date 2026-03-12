// ============================
// Language
// ============================
const AVAILABLE_LANGUAGES = [
  { code: "ko", label: "韓国語" },
  { code: "ja", label: "日本語" },
  { code: "en", label: "English" }
];

const I18N = {
  ko: {
    "login.title": "ログイン",
    "login.subtitle": "お帰りなさい。ログインしてください。",
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
    "signup.sendCode": "認証コード送信",
    "signup.codePlaceholder": "6-digit code",
    "signup.verifyCode": "認証する",
    "signup.alert.codeWrong": "認証コードをもう一度入力してください。",
    "signup.passwordLabel": "パスワード",
    "signup.passwordConfirmLabel": "パスワード確認",
    "signup.nicknameLabel": "ニックネーム",
    "signup.birthDateLabel": "生年月日",
    "signup.genderLabel": "性別",
    "signup.gender.female": "女性",
    "signup.gender.male": "男性",
    "signup.gender.other": "その他",
    "signup.countryLabel": "国",
    "signup.countrySelectPlaceholder": "国 選択",
    "signup.regionLabel": "地域",
    "signup.regionSelectPlaceholder": "地域 選択",
    "signup.regionPlaceholder": "例：釜山、東京、ソウル",
    "signup.submit": "会員登録",
    "signup.haveAccount": "すでにアカウントをお持ちですか？",
    "signup.loginLink": "ログイン",

    "signup.status.verified": "メール認証が完了しました。",
    "signup.alert.emailRequired": "メールアドレスを入力してください.",
    "signup.alert.codeRequired": "6桁の認証コードを入力してください。",
    "signup.alert.codeInvalid": "認証コードは6桁の数字である必要があります。",
    "signup.alert.passwordRequired": "パスワードを入力してください。",
    "signup.alert.passwordConfirmRequired": "パスワード確認を入力してください。",
    "signup.alert.passwordMismatch": "パスワードが一致しません。",
    "signup.alert.nicknameRequired": "ニックネームを入力してください。",
    "signup.alert.birthDateRequired": "生年月日を入力してください。",
    "signup.alert.genderRequired": "性別を選択してください。",
    "signup.alert.countryRequired": "国を選択してください。",
    "signup.alert.regionRequired": "地域を入力または選択してください。",
    "signup.alert.emailNotVerified": "会員登録の前にメール認証を完了してください。",
    "signup.alert.failed": "会員登録に失敗しました。",
    "signup.alert.success": "会員登録が完了しました。",

    "common.confirm": "確認",
    "signup.modal.sendGuideTitle": "認証メール送信のご案内",
    "signup.modal.sendGuideMsg": "認証メールを送信しました。到着まで数分かかる場合がありますので、迷惑メールフォルダもご確認ください。",
    "signup.modal.sentTitle": "認証メールを送信しました。",
    "signup.modal.sentMsg": "送信完了まで少し時間がかかる場合があります。",
    "signup.modal.sendFailTitle": "送信失敗",
    "signup.modal.sendFailMsg": "認証コードの送信に失敗しました。しばらくしてからもう一度お試しください。",
    "signup.status.codeSent": "認証コードを送信しました。メールをご確認ください。"
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
  return localStorage.getItem("lang") || "ja";
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
    KR: ["ソウル","釜山","大邱","仁川","光州","大田","蔚山","世宗","경기","강원","충북","충남","전북","전남","경북","경남","済州"],
    JP: ["東京","大阪","京都","神奈川","埼玉","千葉","愛知","福岡","北海道","沖縄","広島","宮城","静岡"]
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
    US: "はい: 캘리포니아, 뉴욕, 텍사스",
    default: "例：釜山、東京、ソウル"
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

  // 言語 변경 市 国/地域 옵션道 즉市 翻訳 반영
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


function countryNameToCode(name) {
  if (!name) return null;
  const n = name.trim().toLowerCase();

  const map = {
    // KR
    "korea": "KR",
    "韓国": "KR",
    "韓国": "KR",
    "south korea": "KR",
    "republic of korea": "KR",

    // JP
    "japan": "JP",
    "日本": "JP",

    // US
    "usa": "US",
    "united states": "US",
    "united states of america": "US",
    "アメリカ": "US",

    // CN
    "china": "CN",
    "中国": "CN",
  };

  return map[n] || null;
}


const REGION_CODE_MAP = {
    "ソウル特別市": "SEOUL",
    "仁川広域市": "INCHEON",
    "釜山広域市": "BUSAN",
    "大邱広域市": "DAEGU",
    "大田広域市": "DAEJEON",
    "光州広域市": "GWANGJU",
    "蔚山広域市": "ULSAN",
    "世宗特別自治市": "SEJONG",
    "済州特別自治道": "JEJU",
    "京畿道": "GYEONGGI",
    "江原特別自治道": "GANGWON",
    "忠清北道": "CHUNGBUK",
    "忠清南道": "CHUNGNAM",
    "全羅北道": "JEONBUK",
    "全羅南道": "JEONNAM",
    "慶尚北道": "GYEONGBUK",
    "慶尚南道": "GYEONGNAM"
};


function regionNameToCode(input) {
    if (!input) return null;

    const value = input.trim();


    if (REGION_CODE_MAP[value]) {
        return REGION_CODE_MAP[value];
    }


    for (const province in REGION_MAP) {
        const cities = REGION_MAP[province];

        if (cities.includes(value)) {
            return REGION_CODE_MAP[province];
        }
    }

    return null;
}



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
    // ✅ 만 18歳 이상만 選択 가능: max = 오늘 - 18년
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

  // 이메일 변경 市 인증 리셋
  emailInput?.addEventListener("input", () => {
    isEmailVerified = false;
    if (signupBtn) signupBtn.disabled = true;
    if (sendCodeBtn) sendCodeBtn.disabled = false;
    if (verifyCodeBtn) verifyCodeBtn.disabled = false;
    if (emailInput) emailInput.readOnly = false;
    if (codeInput) codeInput.readOnly = false;
    setStatus("");
  });

  // ✅ 인증 코드 전송 (성공 市에만 “전송 안내” 모달)
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

  // ✅ 인증 코드 確認
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

  // ✅ 会員登録 제출 (role 제거됨)
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = emailInput?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";
    const passwordConfirm = document.getElementById("passwordConfirm")?.value ?? "";
    const name = document.getElementById("name")?.value?.trim();

    const birthDate = birthDateInput?.value || "";
    const gender = document.querySelector('input[name="gender"]:checked')?.value ?? null;

    const countryCode = document.getElementById("country")?.value || "";
    
    const region =
      (regionSelect && regionSelect.style.display !== "none"
        ? regionSelect.value
        : regionInput?.value
      )?.trim() || "";
    const regionRaw = regionInput?.value?.trim() || "";
    const regionCode = regionNameToCode(regionRaw);

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
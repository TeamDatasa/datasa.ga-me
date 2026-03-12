
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
    "login.alert.failed": "ログインに失敗しました。"
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
    "login.alert.failed": "ログインに失敗しました。"
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
    "login.alert.failed": "Sign in failed."
  }
};

function getCurrentLang() {
  return localStorage.getItem("lang") || "ja";
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

document.addEventListener("DOMContentLoaded", () => {
  initLanguageSelect();
  initLoginForm();
});

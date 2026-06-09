window.Common = (() => {
    const apiBase = "/api";

    function buildQuery(params = {}) {
        const search = new URLSearchParams();
        Object.entries(params).forEach(([key, value]) => {
            if (value === undefined || value === null) {
                return;
            }
            if (typeof value === "string" && value.trim() === "") {
                return;
            }
            search.append(key, value);
        });
        const text = search.toString();
        return text ? `?${text}` : "";
    }

    async function request(path, options = {}) {
        const init = {
            method: options.method || "GET",
            headers: {},
        };

        if (options.body !== undefined) {
            init.headers["Content-Type"] = "application/json";
            init.body = typeof options.body === "string" ? options.body : JSON.stringify(options.body);
        }

        const response = await fetch(`${apiBase}${path}${buildQuery(options.params)}`, init);
        let payload = {};
        try {
            payload = await response.json();
        } catch (error) {
            payload = {};
        }

        if (!response.ok || payload.code !== 200) {
            throw new Error(payload.msg || `请求失败 (${response.status})`);
        }

        return payload.data;
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#39;");
    }

    function formatDateTime(value) {
        if (!value) {
            return "-";
        }
        return String(value).replace("T", " ");
    }

    function formatDate(value) {
        if (!value) {
            return "-";
        }
        return String(value).slice(0, 10);
    }

    function formatDateTimeInput(value) {
        if (!value) {
            return "";
        }
        const text = String(value).replace(" ", "T");
        return text.length >= 16 ? text.slice(0, 16) : text;
    }

    function formatNumber(value, digits = 2) {
        if (value === undefined || value === null || value === "") {
            return "-";
        }
        const number = Number(value);
        return Number.isFinite(number) ? number.toFixed(digits) : String(value);
    }

    function setClock(elementId = "currentTime") {
        const target = document.getElementById(elementId);
        if (!target) {
            return;
        }
        const refresh = () => {
            target.textContent = new Date().toLocaleString("zh-CN", { hour12: false });
        };
        refresh();
        setInterval(refresh, 1000);
    }

    function renderOptions(select, items, render, placeholder) {
        if (!select) {
            return;
        }
        const parts = [];
        if (placeholder !== undefined) {
            parts.push(`<option value="">${escapeHtml(placeholder)}</option>`);
        }
        items.forEach((item) => {
            parts.push(render(item));
        });
        select.innerHTML = parts.join("");
    }

    function renderPager(container, page, onChange) {
        if (!container) {
            return;
        }
        const total = Number(page.total || 0);
        const pageSize = Number(page.pageSize || 10);
        const current = Number(page.pageNum || 1);
        const pageCount = Math.max(1, Math.ceil(total / pageSize));
        const buttons = [];

        const addButton = (label, target, disabled = false, active = false) => {
            buttons.push(
                `<button class="btn ${active ? "btn-primary" : "btn-secondary"} btn-sm" data-page="${target}" ${disabled ? "disabled" : ""}>${label}</button>`
            );
        };

        addButton("上一页", current - 1, current <= 1);
        for (let pageNo = 1; pageNo <= pageCount; pageNo += 1) {
            if (pageNo === 1 || pageNo === pageCount || Math.abs(pageNo - current) <= 2) {
                addButton(String(pageNo), pageNo, false, pageNo === current);
            }
        }
        addButton("下一页", current + 1, current >= pageCount);

        container.innerHTML = buttons.join("");
        container.querySelectorAll("button[data-page]").forEach((button) => {
            button.addEventListener("click", () => {
                const nextPage = Number(button.dataset.page);
                if (Number.isFinite(nextPage) && nextPage >= 1 && nextPage <= pageCount && nextPage !== current) {
                    onChange(nextPage);
                }
            });
        });
    }

    function openDialog(id) {
        const dialog = document.getElementById(id);
        if (dialog && typeof dialog.showModal === "function") {
            dialog.showModal();
        }
    }

    function closeDialog(id) {
        const dialog = document.getElementById(id);
        if (dialog) {
            dialog.close();
        }
    }

    function fillForm(form, data) {
        Object.entries(data || {}).forEach(([key, value]) => {
            const field = form.querySelector(`[name="${key}"]`);
            if (field) {
                field.value = value ?? "";
            }
        });
    }

    async function loadUsers() {
        const users = await request("/user/list");
        return users.filter((item) => item.status === 1);
    }

    function must(value, message) {
        if (value === undefined || value === null || value === "") {
            throw new Error(message);
        }
        return value;
    }

    return {
        get: (path, params) => request(path, { params }),
        post: (path, body) => request(path, { method: "POST", body }),
        put: (path, body) => request(path, { method: "PUT", body }),
        del: (path, params) => request(path, { method: "DELETE", params }),
        escapeHtml,
        formatDate,
        formatDateTime,
        formatDateTimeInput,
        formatNumber,
        setClock,
        renderOptions,
        renderPager,
        openDialog,
        closeDialog,
        fillForm,
        loadUsers,
        must,
    };
})();

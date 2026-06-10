(function() {
    if (!String.prototype.replaceAll) {
        String.prototype.replaceAll = function(search, replacement) {
            return this.replace(new RegExp(search.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g'), replacement);
        };
    }

    if (!Object.entries) {
        Object.entries = function(obj) {
            var ownProps = Object.keys(obj),
                i = ownProps.length,
                resArray = new Array(i);
            while (i--) {
                resArray[i] = [ownProps[i], obj[ownProps[i]]];
            }
            return resArray;
        };
    }

    window.Common = (function() {
    const apiBase = "/api";

    function buildQuery(params) {
        params = params || {};
        var search = new URLSearchParams();
        Object.entries(params).forEach(function(entry) {
            var key = entry[0], value = entry[1];
            if (value === undefined || value === null) {
                return;
            }
            if (typeof value === "string" && value.trim() === "") {
                return;
            }
            search.append(key, value);
        });
        var text = search.toString();
        return text ? "?" + text : "";
    }

    function isLoginPage() {
        return window.location.pathname.endsWith('/login.html');
    }

    function checkLogin() {
        if (isLoginPage()) {
            return;
        }
        const user = localStorage.getItem('currentUser');
        if (!user) {
            window.location.href = 'login.html';
        }
    }

    function getCurrentUser() {
        const userStr = localStorage.getItem('currentUser');
        return userStr ? JSON.parse(userStr) : null;
    }

    async function logout() {
        try {
            await fetch('/api/user/logout', {
                method: 'POST',
                credentials: 'include'
            });
        } catch (e) {
        }
        localStorage.removeItem('currentUser');
        window.location.href = 'login.html';
    }

    function renderUserInfo(containerId = 'userInfo') {
        const container = document.getElementById(containerId);
        if (!container) return;
        const user = getCurrentUser();
        if (user) {
            container.innerHTML = `
                <span class="me-3"><i class="bi bi-person-circle me-1"></i>${user.realName || user.username}</span>
                <button class="btn btn-outline-light btn-sm" onclick="Common.logout()">
                    <i class="bi bi-box-arrow-right me-1"></i>退出
                </button>
            `;
        }
    }

    async function request(path, options = {}) {
        const init = {
            method: options.method || "GET",
            headers: {},
            credentials: 'include'
        };

        if (options.body !== undefined) {
            init.headers["Content-Type"] = "application/json";
            init.body = typeof options.body === "string" ? options.body : JSON.stringify(options.body);
        }

        const response = await fetch(`${apiBase}${path}${buildQuery(options.params)}`, init);
        
        if (response.status === 401) {
            localStorage.removeItem('currentUser');
            if (!isLoginPage()) {
                window.location.href = 'login.html';
            }
            throw new Error('未登录或登录已过期');
        }

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
        var str = value === undefined || value === null ? "" : String(value);
        return str
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

    function supportsDialog() {
        return typeof HTMLDialogElement !== 'undefined';
    }

    function openDialog(id) {
        var dialog = document.getElementById(id);
        if (!dialog) return;
        
        if (supportsDialog() && typeof dialog.showModal === "function") {
            dialog.showModal();
        } else {
            dialog.style.display = 'block';
            dialog.style.position = 'fixed';
            dialog.style.top = '50%';
            dialog.style.left = '50%';
            dialog.style.transform = 'translate(-50%, -50%)';
            dialog.style.zIndex = '1050';
            dialog.style.background = 'white';
            dialog.style.borderRadius = '8px';
            dialog.style.boxShadow = '0 10px 40px rgba(0,0,0,0.3)';
            
            var overlay = document.createElement('div');
            overlay.id = 'dialog-overlay-' + id;
            overlay.style.position = 'fixed';
            overlay.style.top = '0';
            overlay.style.left = '0';
            overlay.style.width = '100%';
            overlay.style.height = '100%';
            overlay.style.background = 'rgba(0,0,0,0.5)';
            overlay.style.zIndex = '1040';
            document.body.appendChild(overlay);
            
            dialog.dataset.overlayId = 'dialog-overlay-' + id;
        }
    }

    function closeDialog(id) {
        var dialog = document.getElementById(id);
        if (!dialog) return;
        
        if (supportsDialog() && typeof dialog.close === "function") {
            dialog.close();
        } else {
            dialog.style.display = 'none';
            var overlayId = dialog.dataset.overlayId;
            if (overlayId) {
                var overlay = document.getElementById(overlayId);
                if (overlay) {
                    overlay.parentNode.removeChild(overlay);
                }
            }
        }
    }

    function fillForm(form, data) {
        var dataObj = data || {};
        Object.entries(dataObj).forEach(function(entry) {
            var key = entry[0], value = entry[1];
            var field = form.querySelector('[name="' + key + '"]');
            if (field) {
                field.value = value === undefined || value === null ? "" : value;
            }
        });
    }

    async function loadUsers() {
        var users = await request("/user/list");
        return users.filter(function(item) {
            return item.status === 1;
        });
    }

    function must(value, message) {
        if (value === undefined || value === null || value === "") {
            throw new Error(message);
        }
        return value;
    }

    return {
        get: function(path, params) { return request(path, { params: params }); },
        post: function(path, body) { return request(path, { method: "POST", body: body }); },
        put: function(path, body) { return request(path, { method: "PUT", body: body }); },
        del: function(path, params) { return request(path, { method: "DELETE", params: params }); },
        checkLogin: checkLogin,
        getCurrentUser: getCurrentUser,
        logout: logout,
        renderUserInfo: renderUserInfo,
        escapeHtml: escapeHtml,
        formatDate: formatDate,
        formatDateTime: formatDateTime,
        formatDateTimeInput: formatDateTimeInput,
        formatNumber: formatNumber,
        setClock: setClock,
        renderOptions: renderOptions,
        renderPager: renderPager,
        openDialog: openDialog,
        closeDialog: closeDialog,
        fillForm: fillForm,
        loadUsers: loadUsers,
        must: must
    };
    })();
})();

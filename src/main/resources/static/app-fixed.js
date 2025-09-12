// Banking Cards Frontend - FIXED VERSION 2025-09-07 16:37:00
// YearMonth Fix Applied - Send as String Format YYYY-MM

// API Base URL
const API_BASE_URL = '/api';

// Global state
let currentUser = null;
let authToken = localStorage.getItem('authToken');
let userCards = [];

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    if (authToken) {
        validateTokenAndShowApp();
    } else {
        showUnauthenticatedUI();
    }
});

// Authentication Functions
async function validateTokenAndShowApp() {
    try {
        const response = await fetch(`${API_BASE_URL}/cards`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            await loadUserProfile();
            showAuthenticatedUI();
        } else {
            localStorage.removeItem('authToken');
            authToken = null;
            showUnauthenticatedUI();
        }
    } catch (error) {
        console.error('Token validation failed:', error);
        localStorage.removeItem('authToken');
        authToken = null;
        showUnauthenticatedUI();
    }
}

async function login(event) {
    event.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            localStorage.setItem('authToken', authToken);
            
            await loadUserProfile();
            showAuthenticatedUI();
            showToast('Вход выполнен успешно!', 'success');
        } else {
            const errorData = await response.json().catch(() => ({ message: 'Ошибка входа' }));
            showToast(errorData.message || 'Неверные учетные данные', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showToast('Ошибка сети. Попробуйте снова.', 'error');
    } finally {
        showLoading(false);
    }
}

async function register(event) {
    event.preventDefault();
    
    const username = document.getElementById('regUsername').value;
    const fullName = document.getElementById('regFullName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, fullName, email, password })
        });
        
        if (response.ok) {
            showToast('Регистрация успешна! Теперь войдите в систему.', 'success');
            showLogin();
        } else {
            const errorData = await response.json().catch(() => ({ message: 'Ошибка регистрации' }));
            showToast(errorData.message || 'Ошибка при регистрации', 'error');
        }
    } catch (error) {
        console.error('Register error:', error);
        showToast('Ошибка сети. Попробуйте снова.', 'error');
    } finally {
        showLoading(false);
    }
}

async function loadUserProfile() {
    try {
        // For now, we'll use the cards endpoint to get user info indirectly
        currentUser = { username: 'User' }; // Placeholder
        document.getElementById('userFullName').textContent = currentUser.username;
    } catch (error) {
        console.error('Failed to load user profile:', error);
    }
}

function logout() {
    localStorage.removeItem('authToken');
    authToken = null;
    currentUser = null;
    userCards = [];
    showUnauthenticatedUI();
    showToast('Вы вышли из системы', 'info');
}

// UI Management Functions
function showLogin() {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(form => form.style.display = 'none');
    
    document.querySelector('.tab-btn[onclick="showLogin()"]').classList.add('active');
    document.getElementById('loginForm').style.display = 'block';
}

function showRegister() {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(form => form.style.display = 'none');
    
    document.querySelector('.tab-btn[onclick="showRegister()"]').classList.add('active');
    document.getElementById('registerForm').style.display = 'block';
}

function showUnauthenticatedUI() {
    document.getElementById('authSection').style.display = 'block';
    document.getElementById('mainApp').style.display = 'none';
    document.getElementById('userMenu').style.display = 'none';
    
    // Clear forms - FIXED
    const loginForm = document.querySelector('#loginForm form');
    const registerForm = document.querySelector('#registerForm form');
    if (loginForm) loginForm.reset();
    if (registerForm) registerForm.reset();
}

function showAuthenticatedUI() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('mainApp').style.display = 'block';
    document.getElementById('userMenu').style.display = 'flex';
    
    // Load initial data
    loadUserCards();
    showSection('cards');
}

function showSection(sectionName) {
    // Update navigation
    document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.section').forEach(section => {
        section.style.display = 'none';
        section.classList.remove('active');
    });
    
    // Show selected section
    const targetBtn = document.querySelector(`.nav-btn[onclick="showSection('${sectionName}')"]`);
    const targetSection = document.getElementById(`${sectionName}Section`);
    
    if (targetBtn) targetBtn.classList.add('active');
    if (targetSection) {
        targetSection.style.display = 'block';
        targetSection.classList.add('active');
    }
    
    // Load section-specific data
    switch (sectionName) {
        case 'cards':
            loadUserCards();
            break;
        case 'transfer':
            loadCardOptionsForTransfer();
            break;
        case 'transactions':
            loadTransactions();
            break;
    }
}

// Cards Management Functions
async function loadUserCards() {
    try {
        const response = await fetch(`${API_BASE_URL}/cards`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            userCards = await response.json();
            displayCards();
        } else if (response.status === 401) {
            logout();
        } else {
            showToast('Ошибка загрузки карт', 'error');
        }
    } catch (error) {
        console.error('Load cards error:', error);
        showToast('Ошибка сети при загрузке карт', 'error');
    }
}

function displayCards() {
    const cardsGrid = document.getElementById('cardsGrid');
    cardsGrid.innerHTML = '';
    
    if (userCards.length === 0) {
        cardsGrid.innerHTML = '<p style="text-align: center; grid-column: 1/-1; color: #666; font-size: 1.1rem; padding: 40px;">У вас пока нет карт. Создайте свою первую карту!</p>';
        return;
    }
    
    userCards.forEach(card => {
        const cardElement = createCardElement(card);
        cardsGrid.appendChild(cardElement);
    });
}

function createCardElement(card) {
    const cardDiv = document.createElement('div');
    cardDiv.className = 'card';
    
    const statusClass = {
        'ACTIVE': 'status-active',
        'BLOCKED': 'status-blocked',
        'EXPIRED': 'status-expired'
    }[card.status] || 'status-active';
    
    const statusText = {
        'ACTIVE': 'Активна',
        'BLOCKED': 'Заблокирована',
        'EXPIRED': 'Просрочена'
    }[card.status] || 'Активна';
    
    cardDiv.innerHTML = `
        <div class="card-status ${statusClass}">${statusText}</div>
        <div class="card-header">
            <div class="card-type">${card.cardType === 'DEBIT' ? 'Дебетовая' : 'Кредитная'}</div>
            <div class="card-chip"></div>
        </div>
        <div class="card-number">${formatCardNumber(card.cardNumber)}</div>
        <div class="card-details">
            <div class="card-holder">${card.cardHolderName}</div>
            <div class="card-expiry">${formatExpiryDate(card.expiryDate)}</div>
        </div>
        <div class="card-balance">₽${card.balance.toLocaleString('ru-RU', { minimumFractionDigits: 2 })}</div>
        <div class="card-actions">
            <button onclick="toggleCardStatus(${card.id})" ${card.status === 'EXPIRED' ? 'disabled' : ''}>
                ${card.status === 'ACTIVE' ? 'Заблокировать' : card.status === 'BLOCKED' ? 'Разблокировать' : 'Просрочена'}
            </button>
            <button onclick="deleteCard(${card.id})" style="background: rgba(255,107,107,0.2); color: #ff6b6b;">
                Удалить
            </button>
        </div>
    `;
    
    return cardDiv;
}

function formatCardNumber(cardNumber) {
    // Mask the card number: show only last 4 digits
    const lastFour = cardNumber.slice(-4);
    return `**** **** **** ${lastFour}`;
}

function formatExpiryDate(expiryDate) {
    // Handle both object format and string format
    if (typeof expiryDate === 'object' && expiryDate.monthValue && expiryDate.year) {
        return `${String(expiryDate.monthValue).padStart(2, '0')}/${String(expiryDate.year).slice(-2)}`;
    } else if (typeof expiryDate === 'string') {
        const [year, month] = expiryDate.split('-');
        return `${month}/${year.slice(-2)}`;
    }
    return `${String(expiryDate.monthValue).padStart(2, '0')}/${String(expiryDate.year).slice(-2)}`;
}

function showCreateCardForm() {
    document.getElementById('createCardModal').classList.add('show');
}

function hideCreateCardForm() {
    document.getElementById('createCardModal').classList.remove('show');
    document.querySelector('#createCardModal form').reset();
}

async function createCard(event) {
    event.preventDefault();
    
    console.log('Using FIXED createCard function - v2025-09-07-16:37');
    
    const cardHolderName = document.getElementById('cardHolderName').value;
    const cardType = document.getElementById('cardType').value;
    const expiryMonth = parseInt(document.getElementById('cardExpiryMonth').value);
    const expiryYear = parseInt(document.getElementById('cardExpiryYear').value);
    
    // Generate dummy card number for demo
    const cardNumber = '1234567890' + String(Date.now()).slice(-6);
    
    // FIXED: Send expiryDate as string in YYYY-MM format
    const cardData = {
        cardHolderName,
        cardType,
        cardNumber,
        expiryDate: `${expiryYear}-${String(expiryMonth).padStart(2, '0')}`
    };
    
    console.log('FIXED: Sending card data with expiryDate format:', cardData.expiryDate);
    console.log('Full card data:', JSON.stringify(cardData, null, 2));
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/cards`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(cardData)
        });
        
        if (response.ok) {
            hideCreateCardForm();
            showToast('Карта успешно создана!', 'success');
            await loadUserCards();
        } else if (response.status === 401) {
            logout();
        } else {
            const errorData = await response.json().catch(() => ({ message: 'Ошибка создания карты' }));
            showToast(errorData.message || 'Ошибка при создании карты', 'error');
        }
    } catch (error) {
        console.error('Create card error:', error);
        showToast('Ошибка сети при создании карты', 'error');
    } finally {
        showLoading(false);
    }
}

async function toggleCardStatus(cardId) {
    const card = userCards.find(c => c.id === cardId);
    if (!card || card.status === 'EXPIRED') return;
    
    const newStatus = card.status === 'ACTIVE' ? 'BLOCKED' : 'ACTIVE';
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/cards/${cardId}/status`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: newStatus })
        });
        
        if (response.ok) {
            showToast(`Карта ${newStatus === 'ACTIVE' ? 'разблокирована' : 'заблокирована'}!`, 'success');
            await loadUserCards();
        } else if (response.status === 401) {
            logout();
        } else {
            showToast('Ошибка изменения статуса карты', 'error');
        }
    } catch (error) {
        console.error('Toggle card status error:', error);
        showToast('Ошибка сети', 'error');
    } finally {
        showLoading(false);
    }
}

async function deleteCard(cardId) {
    if (!confirm('Вы уверены, что хотите удалить эту карту? Это действие необратимо.')) {
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/cards/${cardId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            showToast('Карта удалена!', 'success');
            await loadUserCards();
        } else if (response.status === 401) {
            logout();
        } else {
            showToast('Ошибка удаления карты', 'error');
        }
    } catch (error) {
        console.error('Delete card error:', error);
        showToast('Ошибка сети', 'error');
    } finally {
        showLoading(false);
    }
}

// Transfer Functions
function loadCardOptionsForTransfer() {
    const fromCardSelect = document.getElementById('fromCard');
    const toCardSelect = document.getElementById('toCard');
    
    // Clear existing options
    fromCardSelect.innerHTML = '<option value="">Выберите карту</option>';
    toCardSelect.innerHTML = '<option value="">Выберите карту</option>';
    
    // Add active cards only
    const activeCards = userCards.filter(card => card.status === 'ACTIVE');
    activeCards.forEach(card => {
        const option = `<option value="${card.id}">${formatCardNumber(card.cardNumber)} (₽${card.balance.toFixed(2)})</option>`;
        fromCardSelect.innerHTML += option;
        toCardSelect.innerHTML += option;
    });
}

async function transferMoney(event) {
    event.preventDefault();
    
    const fromCardId = parseInt(document.getElementById('fromCard').value);
    const toCardId = parseInt(document.getElementById('toCard').value);
    const amount = parseFloat(document.getElementById('transferAmount').value);
    const description = document.getElementById('transferDescription').value;
    
    if (fromCardId === toCardId) {
        showToast('Нельзя переводить деньги на ту же карту', 'error');
        return;
    }
    
    const transferData = {
        fromCardId,
        toCardId,
        amount,
        description: description || `Перевод между картами`
    };
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/transactions/transfer`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(transferData)
        });
        
        if (response.ok) {
            showToast('Перевод выполнен успешно!', 'success');
            document.querySelector('#transferSection form').reset();
            await loadUserCards(); // Refresh balances
        } else if (response.status === 401) {
            logout();
        } else {
            const errorData = await response.json().catch(() => ({ message: 'Ошибка перевода' }));
            showToast(errorData.message || 'Ошибка при выполнении перевода', 'error');
        }
    } catch (error) {
        console.error('Transfer error:', error);
        showToast('Ошибка сети при выполнении перевода', 'error');
    } finally {
        showLoading(false);
    }
}

// Transactions Functions
async function loadTransactions() {
    try {
        const response = await fetch(`${API_BASE_URL}/transactions`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            const transactions = await response.json();
            displayTransactions(transactions);
        } else if (response.status === 401) {
            logout();
        } else {
            showToast('Ошибка загрузки транзакций', 'error');
        }
    } catch (error) {
        console.error('Load transactions error:', error);
        showToast('Ошибка сети при загрузке транзакций', 'error');
    }
}

function displayTransactions(transactions) {
    const transactionsList = document.getElementById('transactionsList');
    transactionsList.innerHTML = '';
    
    if (transactions.length === 0) {
        transactionsList.innerHTML = '<p style="text-align: center; color: #666; font-size: 1.1rem; padding: 40px;">Транзакций пока нет</p>';
        return;
    }
    
    transactions.forEach(transaction => {
        const transactionElement = createTransactionElement(transaction);
        transactionsList.appendChild(transactionElement);
    });
}

function createTransactionElement(transaction) {
    const transactionDiv = document.createElement('div');
    transactionDiv.className = 'transaction';
    
    const isIncoming = userCards.some(card => card.id === transaction.toCardId);
    const isOutgoing = userCards.some(card => card.id === transaction.fromCardId);
    
    let typeIcon = '';
    let amountClass = '';
    let amountPrefix = '';
    
    if (isIncoming && isOutgoing) {
        typeIcon = '↔️';
        amountClass = 'amount-positive';
        amountPrefix = '';
    } else if (isIncoming) {
        typeIcon = '↗️';
        amountClass = 'amount-positive';
        amountPrefix = '+';
    } else {
        typeIcon = '↙️';
        amountClass = 'amount-negative';
        amountPrefix = '-';
    }
    
    const formattedDate = new Date(transaction.createdAt).toLocaleString('ru-RU');
    
    transactionDiv.innerHTML = `
        <div class="transaction-header">
            <div class="transaction-type">
                <span>${typeIcon}</span>
                <span>${transaction.description || 'Перевод'}</span>
            </div>
            <div class="transaction-amount ${amountClass}">
                ${amountPrefix}₽${transaction.amount.toLocaleString('ru-RU', { minimumFractionDigits: 2 })}
            </div>
        </div>
        <div class="transaction-details">
            Статус: ${getTransactionStatusText(transaction.status)}
        </div>
        <div class="transaction-date">${formattedDate}</div>
    `;
    
    return transactionDiv;
}

function getTransactionStatusText(status) {
    const statusMap = {
        'PENDING': 'В обработке',
        'COMPLETED': 'Завершена',
        'FAILED': 'Неудачно',
        'CANCELLED': 'Отменена'
    };
    return statusMap[status] || status;
}

// Utility Functions
function showLoading(show) {
    const overlay = document.getElementById('loadingOverlay');
    overlay.style.display = show ? 'flex' : 'none';
}

function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}
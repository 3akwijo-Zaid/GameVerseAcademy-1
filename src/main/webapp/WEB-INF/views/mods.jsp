<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, ma.ac.esi.gameverseacademy.model.Mod, ma.ac.esi.gameverseacademy.model.User" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>GameVerse Academy</title>
<link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@500;600;700&family=Inter:wght@300;400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#060810;--surface:#0e1117;--surface2:#151b26;--border:rgba(255,255,255,0.06);--accent:#00d4ff;--accent2:#7c3aed;--accent3:#f59e0b;--text:#e2e8f0;--muted:#64748b;--danger:#ef4444;--success:#10b981}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;overflow-x:hidden}
body::before{content:'';position:fixed;inset:0;background-image:linear-gradient(rgba(0,212,255,0.03) 1px,transparent 1px),linear-gradient(90deg,rgba(0,212,255,0.03) 1px,transparent 1px);background-size:60px 60px;pointer-events:none;z-index:0}
.navbar{position:sticky;top:0;z-index:100;display:flex;align-items:center;justify-content:space-between;padding:0 40px;height:64px;background:rgba(6,8,16,0.9);backdrop-filter:blur(20px);border-bottom:1px solid var(--border)}
.nav-logo{display:flex;align-items:center;gap:10px;font-family:'Rajdhani',sans-serif;font-size:22px;font-weight:700;color:var(--text);text-decoration:none}
.nav-logo .logo-icon{width:32px;height:32px;background:linear-gradient(135deg,var(--accent),var(--accent2));border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:16px;color:#fff}
.nav-logo span{color:var(--accent)}
.nav-right{display:flex;align-items:center;gap:10px}
.nav-user{display:flex;align-items:center;gap:8px;padding:6px 14px;background:var(--surface2);border:1px solid var(--border);border-radius:8px;font-size:13px;color:var(--muted)}
.nav-user strong{color:var(--text)}
.avatar{width:26px;height:26px;border-radius:50%;background:linear-gradient(135deg,var(--accent),var(--accent2));display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:700;color:#fff}
.btn-nav{display:inline-flex;align-items:center;gap:6px;padding:8px 14px;border-radius:8px;font-size:13px;font-weight:500;cursor:pointer;text-decoration:none;transition:all 0.2s;border:1px solid transparent}
.btn-primary{background:var(--accent);color:#000;font-weight:600}
.btn-primary:hover{background:#00bfe6;transform:translateY(-1px)}
.btn-ghost{background:transparent;color:var(--muted);border-color:var(--border)}
.btn-ghost:hover{background:var(--surface2);color:var(--text)}
.btn-outline{background:transparent;color:var(--accent);border-color:rgba(0,212,255,0.3)}
.btn-outline:hover{background:rgba(0,212,255,0.1)}
.hero{position:relative;z-index:1;padding:50px 40px 30px;max-width:1400px;margin:0 auto}
.hero-title{font-family:'Rajdhani',sans-serif;font-size:44px;font-weight:700;line-height:1.1;letter-spacing:-1px}
.hero-title .line2{display:block;background:linear-gradient(90deg,var(--accent),var(--accent2));-webkit-background-clip:text;-webkit-text-fill-color:transparent;background-clip:text}
.hero-sub{margin-top:10px;color:var(--muted);font-size:14px}
.hero-stats{display:flex;gap:32px;margin-top:24px}
.stat-num{font-family:'Rajdhani',sans-serif;font-size:26px;font-weight:700;color:var(--accent)}
.stat-label{font-size:11px;color:var(--muted);text-transform:uppercase;letter-spacing:1px}
.filter-bar{position:relative;z-index:1;display:flex;align-items:center;gap:10px;padding:0 40px 28px;max-width:1400px;margin:0 auto;flex-wrap:wrap}
.search-box{display:flex;align-items:center;gap:8px;background:var(--surface2);border:1px solid var(--border);border-radius:10px;padding:8px 14px;flex:1;min-width:200px;max-width:300px}
.search-box i{color:var(--muted);font-size:13px}
.search-box input{background:none;border:none;outline:none;color:var(--text);font-size:13px;width:100%}
.search-box input::placeholder{color:var(--muted)}
.filter-chip{padding:6px 14px;border-radius:20px;font-size:12px;font-weight:500;cursor:pointer;border:1px solid var(--border);background:var(--surface2);color:var(--muted);transition:all 0.2s}
.filter-chip:hover,.filter-chip.active{border-color:var(--accent);color:var(--accent);background:rgba(0,212,255,0.08)}
.games-section{position:relative;z-index:1;padding:0 40px 60px;max-width:1400px;margin:0 auto}
.section-title{font-family:'Rajdhani',sans-serif;font-size:16px;font-weight:600;color:var(--muted);text-transform:uppercase;letter-spacing:2px;margin-bottom:20px;display:flex;align-items:center;gap:10px}
.section-title::after{content:'';flex:1;height:1px;background:var(--border)}
.games-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:20px}
.game-card{position:relative;border-radius:16px;overflow:hidden;background:var(--surface);border:1px solid var(--border);cursor:pointer;transition:transform 0.3s ease,box-shadow 0.3s ease,border-color 0.3s ease;display:flex;flex-direction:column}
.game-card:hover{transform:translateY(-8px) scale(1.01);box-shadow:0 24px 60px rgba(0,0,0,0.6),0 0 0 1px var(--accent);border-color:var(--accent)}
.card-img-wrap{position:relative;height:190px;overflow:hidden;background:var(--surface2);flex-shrink:0}
.card-img-wrap img{width:100%;height:100%;object-fit:cover;transition:transform 0.5s ease}
.game-card:hover .card-img-wrap img{transform:scale(1.08)}
.card-img-placeholder{width:100%;height:100%;display:flex;align-items:center;justify-content:center;flex-direction:column;gap:8px;background:linear-gradient(135deg,#0e1117 0%,#151b26 100%)}
.card-img-placeholder i{font-size:40px;color:rgba(0,212,255,0.15)}
.card-overlay{position:absolute;inset:0;background:linear-gradient(to top,rgba(6,8,16,0.97) 0%,rgba(6,8,16,0.3) 50%,transparent 100%);opacity:0;transition:opacity 0.3s;display:flex;align-items:flex-end;justify-content:center;padding-bottom:14px;gap:8px;flex-wrap:wrap}
.game-card:hover .card-overlay{opacity:1}
.overlay-btn{display:flex;align-items:center;gap:5px;padding:7px 14px;border-radius:7px;font-size:12px;font-weight:600;text-decoration:none;cursor:pointer;border:none;transition:all 0.2s;backdrop-filter:blur(10px)}
.overlay-edit{background:rgba(0,212,255,0.15);border:1px solid rgba(0,212,255,0.4);color:var(--accent)}
.overlay-edit:hover{background:var(--accent);color:#000}
.overlay-delete{background:rgba(239,68,68,0.15);border:1px solid rgba(239,68,68,0.4);color:var(--danger)}
.overlay-delete:hover{background:var(--danger);color:#fff}
.overlay-buy{background:rgba(16,185,129,0.15);border:1px solid rgba(16,185,129,0.4);color:var(--success)}
.overlay-buy:hover{background:var(--success);color:#fff}
.card-badge{position:absolute;top:12px;left:12px;padding:3px 9px;border-radius:5px;font-size:10px;font-weight:700;text-transform:uppercase;letter-spacing:1px;background:rgba(0,212,255,0.15);border:1px solid rgba(0,212,255,0.3);color:var(--accent);backdrop-filter:blur(8px)}
.card-meta{position:absolute;top:12px;right:12px;width:34px;height:34px;border-radius:7px;display:flex;align-items:center;justify-content:center;font-family:'Rajdhani',sans-serif;font-size:13px;font-weight:700;backdrop-filter:blur(8px)}
.meta-green{background:rgba(16,185,129,0.2);border:1px solid rgba(16,185,129,0.5);color:var(--success)}
.meta-yellow{background:rgba(245,158,11,0.2);border:1px solid rgba(245,158,11,0.5);color:var(--accent3)}
.meta-red{background:rgba(239,68,68,0.2);border:1px solid rgba(239,68,68,0.5);color:var(--danger)}
.card-body{padding:14px 16px 12px;display:flex;flex-direction:column;flex:1}
.card-title{font-family:'Rajdhani',sans-serif;font-size:17px;font-weight:700;color:#fff;margin-bottom:3px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.card-author{font-size:11px;color:var(--muted);margin-bottom:8px}
.card-desc{font-size:12px;color:#94a3b8;line-height:1.5;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;flex:1}
.card-footer{display:flex;align-items:center;justify-content:space-between;padding-top:10px;margin-top:auto;border-top:1px solid var(--border)}
.card-stats{display:flex;gap:12px}
.card-stat{display:flex;align-items:center;gap:4px;font-size:11px;color:var(--muted)}
.card-stat i{font-size:10px;color:var(--accent)}
.card-price{font-family:'Rajdhani',sans-serif;font-size:15px;font-weight:700;color:var(--success)}
.stars{display:flex;gap:2px}
.star-filled{color:#f59e0b;font-size:11px}
.star-empty{color:#374151;font-size:11px}
.empty-state{grid-column:1/-1;text-align:center;padding:80px 20px}
.empty-icon{font-size:60px;color:rgba(0,212,255,0.2);margin-bottom:16px}
.modal-overlay{display:none;position:fixed;inset:0;background:rgba(0,0,0,0.7);z-index:1000;align-items:center;justify-content:center}
.modal-overlay.active{display:flex}
.modal{background:var(--surface);border:1px solid var(--border);border-radius:16px;padding:32px;max-width:360px;width:90%;text-align:center}
.modal h3{font-family:'Rajdhani',sans-serif;font-size:22px;margin-bottom:12px}
.modal p{color:var(--muted);font-size:14px;margin-bottom:24px}
.modal-btns{display:flex;gap:12px;justify-content:center}
.rating-modal{display:none;position:fixed;inset:0;background:rgba(0,0,0,0.75);z-index:1000;align-items:center;justify-content:center}
.rating-modal.active{display:flex}
.rating-box{background:var(--surface);border:1px solid var(--border);border-radius:16px;padding:32px;max-width:400px;width:90%}
.rating-box h3{font-family:'Rajdhani',sans-serif;font-size:22px;margin-bottom:6px}
.rating-box p{color:var(--muted);font-size:13px;margin-bottom:20px}
.star-selector{display:flex;gap:8px;margin-bottom:16px;justify-content:center}
.star-selector i{font-size:28px;color:#374151;cursor:pointer;transition:color 0.15s}
.star-selector i.on{color:#f59e0b}
.rating-comment{width:100%;padding:10px 14px;background:var(--surface2);border:1px solid var(--border);border-radius:10px;color:var(--text);font-size:13px;font-family:inherit;resize:vertical;min-height:70px;margin-bottom:16px}
.rating-comment:focus{outline:none;border-color:var(--accent)}
.rating-btns{display:flex;gap:10px}
@keyframes fadeInUp{from{opacity:0;transform:translateY(20px)}to{opacity:1;transform:translateY(0)}}
.game-card{animation:fadeInUp 0.4s ease both}
.game-card:nth-child(1){animation-delay:.05s}.game-card:nth-child(2){animation-delay:.10s}
.game-card:nth-child(3){animation-delay:.15s}.game-card:nth-child(4){animation-delay:.20s}
.game-card:nth-child(5){animation-delay:.25s}.game-card:nth-child(6){animation-delay:.30s}
::-webkit-scrollbar{width:6px}::-webkit-scrollbar-track{background:var(--bg)}
::-webkit-scrollbar-thumb{background:#1e2533;border-radius:3px}
</style>
</head>
<body>
<%
List<Mod> mods = (List<Mod>) request.getAttribute("mods");
User currentUser = (User) session.getAttribute("user");
String loginName = (currentUser != null) ? currentUser.getLogin() : "Joueur";
String avatarLetter = loginName.length() > 0 ? String.valueOf(loginName.charAt(0)).toUpperCase() : "G";
int totalDownloads = 0;
if (mods != null) for (Mod m : mods) totalDownloads += m.getDownloads();
boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());
%>

<div id="logoutModal" class="modal-overlay">
  <div class="modal">
    <h3>Deconnexion</h3>
    <p>Voulez-vous vraiment vous deconnecter ?</p>
    <div class="modal-btns">
      <button class="btn-nav btn-ghost" onclick="document.getElementById('logoutModal').classList.remove('active')">Annuler</button>
      <form action="<%= request.getContextPath() %>/LogoutController" method="post" style="margin:0">
        <button type="submit" class="btn-nav" style="background:var(--danger);color:#fff;border:none">Deconnecter</button>
      </form>
    </div>
  </div>
</div>

<nav class="navbar">
  <a href="<%= request.getContextPath() %>/mods" class="nav-logo">
    <div class="logo-icon"><i class="fas fa-gamepad"></i></div>
    Game<span>Verse</span>&nbsp;Academy
  </a>
  <div class="nav-right">
    <% if (isAdmin) { %>
    <a href="<%= request.getContextPath() %>/dashboard" class="btn-nav btn-outline"><i class="fas fa-chart-bar"></i> Dashboard</a>
    <a href="<%= request.getContextPath() %>/payments" class="btn-nav btn-outline"><i class="fas fa-credit-card"></i> Paiements</a>
    <a href="<%= request.getContextPath() %>/pdf?type=report" class="btn-nav btn-outline"><i class="fas fa-file-pdf"></i> Rapport</a>
    <a href="<%= request.getContextPath() %>/rawg" class="btn-nav btn-outline" title="Covers auto"><i class="fas fa-magic"></i> Covers</a>
    <% } %>
    <a href="<%= request.getContextPath() %>/cards" class="btn-nav btn-outline"><i class="fas fa-shopping-bag"></i> Mes Achats</a>
    <a href="<%= request.getContextPath() %>/profile" class="btn-nav btn-outline"><i class="fas fa-user"></i> Profil</a>
    <div class="nav-user">
      <div class="avatar"><%= avatarLetter %></div>
      <span><strong><%= loginName %></strong></span>
    </div>
    <% if (isAdmin) { %>
    <a href="<%= request.getContextPath() %>/ModSubmitController" class="btn-nav btn-primary"><i class="fas fa-plus"></i> Ajouter</a>
    <% } %>
    <button class="btn-nav btn-ghost" onclick="document.getElementById('logoutModal').classList.add('active')"><i class="fas fa-power-off"></i></button>
  </div>
</nav>

<div class="hero">
  <div class="hero-title">Bibliotheque des<span class="line2">Jeux &amp; Mods</span></div>
  <p class="hero-sub">Decouvrez, gerez et explorez la collection.</p>
  <div class="hero-stats">
    <div><div class="stat-num"><%= mods != null ? mods.size() : 0 %></div><div class="stat-label">Mods</div></div>
    <div><div class="stat-num"><%= String.format("%,d", totalDownloads) %></div><div class="stat-label">Downloads</div></div>
    <div><div class="stat-num" id="filtered-count"><%= mods != null ? mods.size() : 0 %></div><div class="stat-label">Affiches</div></div>
  </div>
</div>

<div class="filter-bar">
  <div class="search-box"><i class="fas fa-search"></i><input type="text" id="searchInput" placeholder="Rechercher..." oninput="filterCards()"></div>
  <button class="filter-chip active" onclick="setFilter(this,'all')">Tous</button>
  <button class="filter-chip" onclick="setFilter(this,'Action')">Action</button>
  <button class="filter-chip" onclick="setFilter(this,'RPG')">RPG</button>
  <button class="filter-chip" onclick="setFilter(this,'FPS')">FPS</button>
  <button class="filter-chip" onclick="setFilter(this,'Strategy')">Strategie</button>
  <button class="filter-chip" onclick="setFilter(this,'Adventure')">Aventure</button>
  <button class="filter-chip" onclick="setFilter(this,'Sport')">Sport</button>
</div>

<div class="games-section">
  <div class="section-title">Collection</div>
  <div class="games-grid" id="gamesGrid">
<% if (mods != null && !mods.isEmpty()) { for (Mod mod : mods) {
   String metaclass = mod.getMetacritic() >= 75 ? "meta-green" : (mod.getMetacritic() < 50 ? "meta-red" : "meta-yellow");
   String displayImg = mod.getDisplayImage();
   int fullStars = (int) Math.round(mod.getAverageRating());
%>
    <div class="game-card" data-category="<%= mod.getCategory() != null ? mod.getCategory() : "" %>" data-title="<%= mod.getTitle() != null ? mod.getTitle().toLowerCase() : "" %>">
      <div class="card-img-wrap">
        <% if (displayImg != null && !displayImg.isEmpty()) { %>
          <img src="<%= displayImg.startsWith("http") ? displayImg : request.getContextPath() + "/" + displayImg %>" alt="<%= mod.getTitle() %>" loading="lazy">
        <% } else { %>
          <div class="card-img-placeholder"><i class="fas fa-dice-d20"></i></div>
        <% } %>
        <span class="card-badge"><%= mod.getCategory() != null ? mod.getCategory() : "Mod" %></span>
        <% if (mod.getMetacritic() > 0) { %><div class="card-meta <%= metaclass %>"><%= mod.getMetacritic() %></div><% } %>
        <div class="card-overlay">
          <a href="<%= request.getContextPath() %>/cards?action=buy&modId=<%= mod.getId() %>" class="overlay-btn overlay-buy"><i class="fas fa-shopping-cart"></i> Acheter</a>
          <button type="button" class="overlay-btn" style="background:rgba(245,158,11,0.15);border:1px solid rgba(245,158,11,0.4);color:#f59e0b" onclick="openRating('<%= mod.getId() %>','<%= mod.getTitle() %>')"><i class='fas fa-star'></i> Noter</button>
          <% if (isAdmin) { %>
          <a href="<%= request.getContextPath() %>/ModUpdateController?id=<%= mod.getId() %>" class="overlay-btn overlay-edit"><i class="fas fa-pen"></i></a>
          <a href="<%= request.getContextPath() %>/ModDeleteController?id=<%= mod.getId() %>" class="overlay-btn overlay-delete" onclick="return confirm('Supprimer ce mod ?')"><i class="fas fa-trash"></i></a>
          <% } %>
        </div>
      </div>
      <div class="card-body">
        <div class="card-title"><%= mod.getTitle() %></div>
        <div class="card-author"><i class="fas fa-user" style="color:var(--accent);font-size:10px;margin-right:4px"></i><%= mod.getAuthor() %><% if (mod.getPublisher() != null && !mod.getPublisher().isEmpty()) { %> &middot; <%= mod.getPublisher() %><% } %></div>
        <p class="card-desc"><%= mod.getDescription() != null && !mod.getDescription().isEmpty() ? mod.getDescription() : "Aucune description." %></p>
        <div class="card-footer">
          <div class="card-stats">
            <span class="card-stat"><i class="fas fa-download"></i><%= String.format("%,d", mod.getDownloads()) %></span>
            <span class="stars"><% for (int s=1;s<=5;s++) { %><i class="fas fa-star <%= s <= fullStars ? "star-filled" : "star-empty" %>"></i><% } %></span>
          </div>
          <span class="card-price"><%= mod.getPrice() > 0 ? String.format("%.2f EUR", mod.getPrice()) : "Gratuit" %></span>
        </div>
      </div>
    </div>
<% } } else { %>
    <div class="empty-state"><div class="empty-icon"><i class="fas fa-dice-d20"></i></div><p>Bibliotheque vide.</p></div>
<% } %>
  </div>
</div>

<div id="ratingModal" class="rating-modal">
  <div class="rating-box">
    <h3>Noter ce jeu</h3>
    <p id="ratingModTitle" style="color:var(--accent);font-weight:600"></p>
    <form action="<%= request.getContextPath() %>/rate" method="POST">
      <input type="hidden" name="modId" id="ratingModId">
      <input type="hidden" name="stars" id="ratingStarsInput" value="5">
      <div class="star-selector" id="starSelector">
        <i class="fas fa-star on" data-val="1"></i>
        <i class="fas fa-star on" data-val="2"></i>
        <i class="fas fa-star on" data-val="3"></i>
        <i class="fas fa-star on" data-val="4"></i>
        <i class="fas fa-star on" data-val="5"></i>
      </div>
      <textarea class="rating-comment" name="comment" placeholder="Votre avis (optionnel)..."></textarea>
      <div class="rating-btns">
        <button type="button" class="btn-nav btn-ghost" onclick="closeRating()">Annuler</button>
        <button type="submit" class="btn-nav btn-primary"><i class="fas fa-star"></i> Envoyer</button>
      </div>
    </form>
  </div>
</div>
<script>
let cf='all';
function setFilter(btn,cat){cf=cat;document.querySelectorAll('.filter-chip').forEach(c=>c.classList.remove('active'));btn.classList.add('active');filterCards();}
function filterCards(){const q=document.getElementById('searchInput').value.toLowerCase();let v=0;document.querySelectorAll('.game-card').forEach(c=>{const m=(cf==='all'||(c.dataset.category||'').toLowerCase().includes(cf.toLowerCase()))&&(c.dataset.title||'').includes(q);c.style.display=m?'':'none';if(m)v++;});document.getElementById('filtered-count').textContent=v;}
function openRating(modId,modTitle){document.getElementById('ratingModId').value=modId;document.getElementById('ratingModTitle').textContent=modTitle;document.getElementById('ratingModal').classList.add('active');}
function closeRating(){document.getElementById('ratingModal').classList.remove('active');}
document.querySelectorAll('#starSelector i').forEach(s=>{s.addEventListener('click',()=>{const val=parseInt(s.dataset.val);document.getElementById('ratingStarsInput').value=val;document.querySelectorAll('#starSelector i').forEach(x=>x.classList.toggle('on',parseInt(x.dataset.val)<=val));});});
</script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, ma.ac.esi.gameverseacademy.model.Mod, ma.ac.esi.gameverseacademy.model.Payment" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Dashboard — GameVerse</title>
<link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#060810;--surface:#0e1117;--surface2:#151b26;--border:rgba(255,255,255,0.06);--accent:#00d4ff;--accent2:#7c3aed;--text:#e2e8f0;--muted:#64748b;--success:#10b981;--danger:#ef4444;--warn:#f59e0b}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;padding:40px}
body::before{content:'';position:fixed;inset:0;background-image:linear-gradient(rgba(0,212,255,0.02) 1px,transparent 1px),linear-gradient(90deg,rgba(0,212,255,0.02) 1px,transparent 1px);background-size:60px 60px;pointer-events:none}
.page-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:36px;max-width:1200px}
.page-title{font-family:'Rajdhani',sans-serif;font-size:34px;font-weight:700}
.page-title span{color:var(--accent)}
.back-link{display:inline-flex;align-items:center;gap:6px;color:var(--muted);font-size:13px;text-decoration:none;padding:8px 14px;border:1px solid var(--border);border-radius:8px;transition:all 0.2s}
.back-link:hover{border-color:var(--accent);color:var(--accent)}
.stats-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;max-width:1200px;margin-bottom:32px}
.stat-card{background:var(--surface);border:1px solid var(--border);border-radius:14px;padding:24px;position:relative;overflow:hidden;transition:border-color 0.2s}
.stat-card:hover{border-color:rgba(0,212,255,0.3)}
.stat-card::before{content:'';position:absolute;top:0;left:0;right:0;height:2px}
.stat-card.blue::before{background:var(--accent)}
.stat-card.purple::before{background:var(--accent2)}
.stat-card.green::before{background:var(--success)}
.stat-card.warn::before{background:var(--warn)}
.stat-icon{width:40px;height:40px;border-radius:10px;display:flex;align-items:center;justify-content:center;font-size:18px;margin-bottom:14px}
.stat-card.blue .stat-icon{background:rgba(0,212,255,0.1);color:var(--accent)}
.stat-card.purple .stat-icon{background:rgba(124,58,237,0.1);color:var(--accent2)}
.stat-card.green .stat-icon{background:rgba(16,185,129,0.1);color:var(--success)}
.stat-card.warn .stat-icon{background:rgba(245,158,11,0.1);color:var(--warn)}
.stat-value{font-family:'Rajdhani',sans-serif;font-size:30px;font-weight:700;margin-bottom:4px}
.stat-label{font-size:12px;color:var(--muted)}
.two-col{display:grid;grid-template-columns:1fr 1fr;gap:20px;max-width:1200px}
.panel{background:var(--surface);border:1px solid var(--border);border-radius:14px;padding:24px}
.panel-title{font-family:'Rajdhani',sans-serif;font-size:18px;font-weight:600;margin-bottom:16px;color:var(--accent)}
.list-item{display:flex;align-items:center;justify-content:space-between;padding:10px 0;border-bottom:1px solid var(--border);font-size:13px}
.list-item:last-child{border-bottom:none}
.list-item .name{font-weight:500}
.list-item .val{color:var(--success);font-weight:600}
.list-item .muted{color:var(--muted)}
</style>
</head>
<body>
<div class="page-header">
  <div class="page-title">Admin <span>Dashboard</span></div>
  <a href="<%= request.getContextPath() %>/mods" class="back-link"><i class="fas fa-arrow-left"></i> Retour</a>
</div>
<div class="stats-grid">
  <div class="stat-card blue"><div class="stat-icon"><i class="fas fa-gamepad"></i></div><div class="stat-value"><%= request.getAttribute("totalMods") %></div><div class="stat-label">Total Mods</div></div>
  <div class="stat-card purple"><div class="stat-icon"><i class="fas fa-shopping-cart"></i></div><div class="stat-value"><%= request.getAttribute("totalSales") %></div><div class="stat-label">Ventes totales</div></div>
  <div class="stat-card green"><div class="stat-icon"><i class="fas fa-euro-sign"></i></div><div class="stat-value"><%= request.getAttribute("totalRevenue") %></div><div class="stat-label">Chiffre d'affaires (EUR)</div></div>
  <div class="stat-card warn"><div class="stat-icon"><i class="fas fa-download"></i></div><div class="stat-value"><%= request.getAttribute("totalDownloads") %></div><div class="stat-label">Telechargements</div></div>
</div>
<div class="two-col">
  <div class="panel">
    <div class="panel-title"><i class="fas fa-gamepad"></i> Derniers Mods</div>
    <% List<Mod> recentMods = (List<Mod>) request.getAttribute("recentMods"); if(recentMods!=null) for(Mod m:recentMods) { %>
    <div class="list-item"><span class="name"><%= m.getTitle() %></span><span class="muted"><%= m.getCategory() %></span></div>
    <% } %>
  </div>
  <div class="panel">
    <div class="panel-title"><i class="fas fa-credit-card"></i> Derniers Paiements</div>
    <% List<Payment> recentPay = (List<Payment>) request.getAttribute("recentPayments"); if(recentPay!=null) for(Payment p:recentPay) { %>
    <div class="list-item"><span class="name"><%= p.getModTitle() %></span><span class="val"><%= String.format("%.2f EUR", p.getAmount()) %></span></div>
    <% } %>
  </div>
</div>
</body>
</html>

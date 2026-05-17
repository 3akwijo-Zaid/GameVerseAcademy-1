<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ajouter — GameVerse</title>
<link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#060810;--surface:#0e1117;--surface2:#151b26;--border:rgba(255,255,255,0.07);--accent:#00d4ff;--accent2:#7c3aed;--text:#e2e8f0;--muted:#64748b;--danger:#ef4444}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;display:flex;align-items:center;justify-content:center;padding:40px 20px}
body::before{content:'';position:fixed;inset:0;background-image:linear-gradient(rgba(0,212,255,0.025) 1px,transparent 1px),linear-gradient(90deg,rgba(0,212,255,0.025) 1px,transparent 1px);background-size:60px 60px;pointer-events:none}
.container{position:relative;z-index:1;width:100%;max-width:620px;background:var(--surface);border:1px solid var(--border);border-radius:20px;padding:36px;box-shadow:0 40px 80px rgba(0,0,0,0.5)}
.header{display:flex;align-items:center;gap:12px;margin-bottom:28px}
.header .icon{width:44px;height:44px;border-radius:10px;background:linear-gradient(135deg,var(--accent),var(--accent2));display:flex;align-items:center;justify-content:center;font-size:18px;color:#fff}
.header h1{font-family:'Rajdhani',sans-serif;font-size:24px;font-weight:700}
.header p{font-size:12px;color:var(--muted);margin-top:2px}
.form-row{display:grid;grid-template-columns:1fr 1fr;gap:14px}
.form-group{margin-bottom:16px}
label{display:block;font-size:11px;font-weight:600;color:var(--muted);text-transform:uppercase;letter-spacing:1px;margin-bottom:7px}
input[type="text"],input[type="number"],textarea,select{width:100%;padding:11px 14px;background:var(--surface2);border:1px solid var(--border);border-radius:10px;color:var(--text);font-size:14px;font-family:inherit;transition:border-color 0.2s}
input:focus,textarea:focus,select:focus{outline:none;border-color:var(--accent);box-shadow:0 0 0 3px rgba(0,212,255,0.1)}
textarea{resize:vertical;min-height:90px}
select option{background:#1a1f2e}
.upload-zone{border:2px dashed var(--border);border-radius:12px;padding:28px 20px;text-align:center;cursor:pointer;transition:all 0.2s;position:relative;background:var(--surface2)}
.upload-zone:hover{border-color:var(--accent);background:rgba(0,212,255,0.04)}
.upload-zone input{position:absolute;inset:0;opacity:0;cursor:pointer;width:100%;height:100%}
.upload-zone i{font-size:26px;color:var(--muted);margin-bottom:8px}
.upload-zone p{font-size:13px;color:var(--muted)}
.upload-zone small{font-size:11px;color:var(--muted);opacity:0.6}
#preview-wrap{margin-top:10px;display:none}
#preview-wrap img{width:100%;max-height:150px;object-fit:cover;border-radius:8px}
.alert{padding:11px 14px;border-radius:9px;font-size:13px;margin-bottom:16px}
.alert-error{background:rgba(239,68,68,0.1);border:1px solid rgba(239,68,68,0.3);color:var(--danger)}
.rawg-note{display:flex;align-items:center;gap:6px;font-size:11px;color:var(--muted);margin-top:8px}
.rawg-note i{color:var(--accent)}
.btn-submit{width:100%;padding:14px;background:linear-gradient(135deg,var(--accent),var(--accent2));border:none;border-radius:10px;color:#fff;font-size:15px;font-weight:700;font-family:'Rajdhani',sans-serif;cursor:pointer;letter-spacing:1px;text-transform:uppercase;transition:opacity 0.2s,transform 0.2s;margin-top:8px}
.btn-submit:hover{opacity:0.9;transform:translateY(-1px)}
.back-link{display:inline-flex;align-items:center;gap:6px;color:var(--muted);font-size:13px;text-decoration:none;margin-top:14px;transition:color 0.2s}
.back-link:hover{color:var(--accent)}
</style>
</head>
<body>
<div class="container">
  <div class="header"><div class="icon"><i class="fas fa-plus"></i></div><div><h1>Ajouter un Mod</h1><p>Enrichissez la bibliotheque GameVerse</p></div></div>
  <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %></div><% } %>
  <form action="<%= request.getContextPath() %>/ModSubmitController" method="POST" enctype="multipart/form-data">
    <div class="form-group"><label>Titre du jeu *</label><input type="text" name="title" placeholder="Ex: Elden Ring" required></div>
    <div class="form-row">
      <div class="form-group"><label>Categorie</label>
        <select name="category">
          <% String[] cats = {"Action","RPG","FPS","Strategy","Adventure","Sport","Simulation","Horror","Indie"};
             for (String cat : cats) { %><option value="<%= cat %>"><%= cat %></option><% } %>
        </select>
      </div>
      <div class="form-group"><label>Prix (EUR)</label><input type="number" name="price" placeholder="9.99" step="0.01" min="0" value="0"></div>
    </div>
    <div class="form-row">
      <div class="form-group"><label>Publisher</label><input type="text" name="publisher" placeholder="Ex: FromSoftware"></div>
      <div class="form-group"><label>Plateforme</label><input type="text" name="platform" placeholder="PC / PS5"></div>
    </div>
    <div class="form-row">
      <div class="form-group"><label>Date de sortie</label><input type="text" name="releaseDate" placeholder="2024-01-01"></div>
      <div class="form-group"><label>Score Metacritic</label><input type="number" name="metacritic" placeholder="85" min="0" max="100"></div>
    </div>
    <div class="form-row">
      <div class="form-group"><label>Telechargements</label><input type="number" name="downloads" placeholder="0" min="0"></div>
    </div>
    <div class="form-group"><label>Description</label><textarea name="description" placeholder="Decrivez ce jeu..."></textarea></div>
    <div class="form-group">
      <label>Image du jeu</label>
      <div class="upload-zone" id="uploadZone">
        <input type="file" name="image" accept="image/*" onchange="previewImage(this)">
        <i class="fas fa-cloud-upload-alt"></i>
        <p>Cliquez ou glissez une image</p>
        <small>JPG, PNG, WEBP — Max 5 MB</small>
      </div>
      <div id="preview-wrap"><img id="preview-img" src="" alt="Apercu"></div>
      <div class="rawg-note"><i class="fas fa-magic"></i> Si pas d'image, la cover sera recuperee automatiquement depuis RAWG</div>
    </div>
    <button type="submit" class="btn-submit"><i class="fas fa-upload"></i> &nbsp;Publier le Mod</button>
  </form>
  <a href="<%= request.getContextPath() %>/mods" class="back-link"><i class="fas fa-arrow-left"></i> Retour</a>
</div>
<script>
function previewImage(input) {
  if (input.files[0]) {
    const reader = new FileReader();
    reader.onload = e => { document.getElementById('preview-img').src = e.target.result; document.getElementById('preview-wrap').style.display='block'; };
    reader.readAsDataURL(input.files[0]);
  }
}
</script>
</body>
</html>

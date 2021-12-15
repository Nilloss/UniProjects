from webapp import db, init_app

app = init_app()
context = app.app_context()
context.push()

db.create_all()
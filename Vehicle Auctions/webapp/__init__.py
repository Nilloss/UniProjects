from flask import Flask

from flask_bootstrap import Bootstrap
from flask_sqlalchemy import SQLAlchemy
from flask_login import LoginManager

db = SQLAlchemy()
login_manager = LoginManager()
app = Flask(__name__)
from webapp import error_handling

def init_app() -> Flask:
    app.secret_key = '1738'

    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.sqlite'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    UPLOAD_FOLDER = '/static/img'
    app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

    login_manager.login_view = 'authentication.login'
    login_manager.init_app(app)

    # Allow Bootstrap in HTML Online Server.
    bootstrap = Bootstrap(app)

    db.init_app(app)

    # Register all blueprints here

    from webapp.views import mainbp
    from webapp.authentication import auth_bp
    from webapp.listing import listing_bp
    from webapp.watchlist import watchlist_bp
    from webapp.manage import manage_bp

    app.register_blueprint(mainbp)
    app.register_blueprint(auth_bp)
    app.register_blueprint(listing_bp)
    app.register_blueprint(watchlist_bp)
    app.register_blueprint(manage_bp)

    return app
from webapp import db, login_manager
from flask_login import UserMixin

class User(UserMixin, db.Model):
    __tablename__ = 'user'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), index=True, unique=True, nullable=False)
    email = db.Column(db.String(100), index=True, nullable=False)
    password_hash = db.Column(db.String(255), nullable=False)
    phone_numb = db.Column(db.Integer(), index=True, nullable=False)
    address = db.Column(db.String(200), index=True, nullable=False)
    
    listings = db.relationship('Listing', backref='user')

@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))

from webapp import db
from datetime import datetime

class Bid(db.Model):
    __tablename__ = 'bid'

    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    listing_id = db.Column(db.Integer, db.ForeignKey('listing.id'))
    amount = db.Column(db.Integer, nullable=False)
    date = db.Column(db.DateTime, default=datetime.now())
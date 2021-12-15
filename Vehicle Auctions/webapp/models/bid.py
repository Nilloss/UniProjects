from webapp import db
from datetime import datetime


class Bid(db.Model):
    __tablename__ = 'bid'
    
    id = db.Column(db.Integer, primary_key=True)
    amount = db.Column(db.Integer, nullable=False)
    date = db.Column(db.DateTime, default=datetime.now())

    #Link Listing Table and user table
    listing_id = db.Column(db.Integer, db.ForeignKey('listing.id'))
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    
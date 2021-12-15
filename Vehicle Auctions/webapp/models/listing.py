from webapp import db
from datetime import datetime


class Listing(db.Model):
    __tablename__ = 'listing'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    name = db.Column(db.String(249), nullable=False)
    starting_bid = db.Column(db.Integer, default=0, nullable=False)
    make = db.Column(db.String(100))
    model = db.Column(db.String(100), nullable=False)
    year = db.Column(db.Integer, nullable=False)
    body_type = db.Column(db.String(100))
    transmission = db.Column(db.String(100))
    fuel_type = db.Column(db.String(100))
    description = db.Column(db.String(500), nullable=False)
    image_data = db.Column(db.String(400), nullable=False)
    date_listed = db.Column(db.DateTime, default=datetime.now())
    closing_date = db.Column(db.DateTime, nullable=False)
    state = db.Column(db.Enum('Open', 'Closed'), default='Open')

    total_bids = db.Column(db.Integer, default=0, nullable=False)
    current_bid = db.Column(db.Integer, default=starting_bid, nullable=False)
    bids = db.relationship('Bid', backref='bids')
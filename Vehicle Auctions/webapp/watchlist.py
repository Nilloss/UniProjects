from webapp.models.user import User
from webapp.models.listing import Listing
from webapp import db
from webapp.models.watchlist import Watchlist
from flask import Blueprint, render_template, url_for, redirect, session, flash
from flask_login import login_required, current_user

watchlist_bp = Blueprint('watchlist', __name__ , url_prefix='/watchlist')

@watchlist_bp.route('/')
@login_required
def show():
    id = current_user.get_id()
    user_query = Watchlist.query.filter_by(user_id=id).all()
    items = [0] * len(user_query)
    turn = 0
    for query in user_query:
        items[turn] = Listing.query.filter_by(id=query.listing_id).first()
        turn+= 1

    return render_template('watchlist.html', listings=items)
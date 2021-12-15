from flask import Blueprint, render_template, url_for, session, flash, redirect
from datetime import datetime
from webapp.models.listing import Listing
from flask_login import login_required, current_user

manage_bp = Blueprint('manage', __name__, url_prefix='/manage')


@manage_bp.route("/")
@login_required
def show():
    id = current_user.get_id()
    listings = Listing.query.filter_by(user_id=id).all()
    return render_template('listing/manage.html', listings=listings)

# BUTTON TO REMOVE NOT ACTUAL PAGE!!
@manage_bp.route('/<id>/close')
@login_required
def remove(id):
    user_query = Listing.query.filter_by(id=id).first()
    if user_query.user_id == current_user.get_id():
        listing = session.query(Listing).get(id)
        listing.closing_date = datetime.now()
        listing.state = "Closed"
        session.commit()
    else:
        flash("Sorry, only the original lister can close the bid")
        
    redirect((url_for('listing.show', id=id)))

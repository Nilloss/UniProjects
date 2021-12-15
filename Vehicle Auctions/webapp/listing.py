from flask import Blueprint, render_template, url_for, redirect, session, flash
from webapp.models.forms import ListingForm, BidForm
from webapp.models.listing import Listing
from webapp.models.bids import Bid
from webapp.models.watchlist import Watchlist
from flask_login import login_required, current_user
from webapp import db
from datetime import datetime
#for file upload
from werkzeug.utils import secure_filename
import os

listing_bp = Blueprint('listing', __name__, url_prefix='/listing')

@listing_bp.route('/<id>', methods=['GET', 'POST'])
def show(id):
    listing = Listing.query.filter_by(id=id).first()
    bid = BidForm()
    return render_template('listing/show.html', listing=listing, form=bid)


# Create Listing for database
@listing_bp.route('/create', methods=['GET', 'POST'])
@login_required
def create():
    list_instance = ListingForm()
    if list_instance.validate_on_submit():
        listing = Listing()
        listing.name = list_instance.name.data
        listing.starting_bid = list_instance.startingBid.data
        listing.make = list_instance.make.data
        listing.year = list_instance.year.data
        listing.body_type = list_instance.body_type.data
        listing.model = list_instance.model.data
        listing.transmission = list_instance.transmission.data
        listing.fuel_type = list_instance.fuel_type.data
        listing.description = list_instance.description.data
        listing.closing_date = list_instance.closing_date.data
        listing.user_id = current_user.get_id()
        listing.current_bid = list_instance.startingBid.data

        # get file data from form
        fp = listing.image_data
        filename = fp.filename
        # get the current path of the module file… store image file relative to this path
        BASE_PATH = os.path.dirname(__file__)
        #upload file location – directory of this file/static/image
        upload_path = os.path.join(BASE_PATH, 'static/img', secure_filename(filename))
        # store relative path in DB as image location in HTML is relative
        db_upload_path = '/static/img/'+ secure_filename(filename)
        # save the file and return the db upload path
        fp.save(upload_path)
        listing.image_data = db_upload_path

        db.session.add(listing)
        db.session.commit()
        return redirect(url_for('listing.show', id=listing.id))
        
    return render_template('listing/create.html', form=list_instance)

#Edit Listing details
@listing_bp.route('/<id>/edit')
@login_required
def edit(id):
    user_id = current_user.get_id()
    listing = Listing.query.filter_by(id=id).first()
    if user_id == listing.user_id:
        list_instance = ListingForm()
        if list_instance.validate_on_submit():
            listing.name = list_instance.name.data
            listing.starting_bid = list_instance.startingBid.data
            listing.make = list_instance.make.data
            listing.year = list_instance.year.data
            listing.body_type = list_instance.body_type.data
            listing.model = list_instance.model.data
            listing.transmission = list_instance.transmission.data
            listing.fuel_type = list_instance.fuel_type.data
            listing.description = list_instance.description.data
            listing.image_data = list_instance.image_data.data
            listing.closing_date = list_instance.closing_date.data
            listing.user_id = current_user.get_id()
            listing.current_bid = list_instance.startingBid.data
            db.session.commit()
            return redirect(url_for('listing.show', id=id))
        
        return redirect(url_for('listing.edit', id=id))

    return redirect(url_for('listing.show', id=id))




# Create Bid
@listing_bp.route('/<id>/bid', methods=['GET', 'POST'] )
@login_required
def bid(id):
    bid_instance = BidForm()
    bid = Bid()

    if bid_instance.validate_on_submit():
        listing = Listing.query.filter_by(id=id).first()
        bid.user_id = current_user.get_id()
        bid.amount = bid_instance.amount.data
        bid.listing_id = listing.id
        current_bid = listing.current_bid

        if current_bid < bid.amount:
            listing.current_bid = bid.amount
            listing.total_bids = listing.total_bids + 1
            db.session.add(bid)
            db.session.commit()
            flash("Bid accepted!")
        else:
            flash("You must bid higher than the current bid")

        return redirect(url_for('listing.show', id=id))
        
    return redirect(url_for('listing.show', id=id, form=bid_instance))

#BUTTON TO ADD NOT ACTUAL SITE!!
@listing_bp.route('/<id>/add')
@login_required
def add(id):
    watch_item = Watchlist()
    watch_item.user_id = current_user.get_id()
    watch_item.listing_id = id

    watchlist = Watchlist.query.filter_by(id=id, user_id=current_user.get_id())
    if watchlist == None:
        return redirect(url_for('listing.show', id=id))

    watchlist = Watchlist.query.filter_by(user_id=current_user.get_id(), listing_id=id).count()
    if watchlist != 0:
        flash("You are already watching this item!")
    else:
        db.session.add(watch_item)
        db.session.commit()
        flash("Successfully added to watchlist!")

    return redirect(url_for('listing.show', id=id))


# BUTTON TO REMOVE NOT ACTUAL PAGE!!
@listing_bp.route('/<id>/close')
@login_required
def remove(id):
    user_query = Listing.query.filter_by(id=id).first()
    user_query.closing_date = datetime.now()
    user_query.state = "Closed"
    db.session.commit()
    return redirect(url_for('listing.show', id=id))

@listing_bp.route('/<id>/remove')
def watchlist_remove(id):
    user_id = current_user.get_id()
    watchlist = Watchlist.query.filter_by(user_id=user_id, listing_id=id).delete()
    if (watchlist == 0):
        flash("You are not currently watching this listing")
    else:
        db.session.commit()
        flash("Successfully removed from watchlist!")

    return redirect(url_for('listing.show', id=id))
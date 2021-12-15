from flask import Blueprint, render_template, url_for, redirect, session, flash
from webapp.models.forms import ListingForm, BidForm
from webapp.models.listing import Listing
from webapp.models.bids import Bid
from webapp.models.watchlist import Watchlist
from flask_login import login_required, current_user
from webapp import db
from datetime import datetime

listing_bp = Blueprint('listing', __name__, url_prefix='/listing')


@listing_bp.route('/<id>', methods=['GET', 'POST'])
def show(id):
    listing = Listing.query.filter_by(id=id).first
    return render_template('listing/show.html', listing=listing)


# Create Listing for database
@listing_bp.route('/create', methods=['GET', 'POST'])
# @login_required
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
        listing.image_data = list_instance.image_data.data
        listing.closing_date = list_instance.closing_date.data
        listing.user_id = current_user.get_id()
        listing.current_bid = list_instance.startingBid.data

        db.session.add(listing)
        db.session.commit()
        return redirect(url_for('listing.create'))
        
    return render_template('listing/create.html', form=list_instance)

#Edit Listing details
@listing_bp.route('/<id>/edit')
def edit(id):
    user_id = current_user.get_id()
    listing = Listing.query.filter_by(id=id).first
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

        return render_template('listing/create.html', form=list_instance, listing=listing)

    return redirect(url_for('listing.show', id=id))




#Create Bid
@listing_bp.route('/<id>/bid')
# @login_required
def bid(id):
    bid_instance = BidForm()
    if bid_instance.validate_on_submit():
        listing = Listing.query.filter_by(id=id).first
        bid = Bid()
        bid.user_id = current_user.get_id()
        bid.amount = bid_instance.data
        listing = Listing.qurey.filter_by(id=id).first
        if bid.amount < listing.current_bid:
            flash('Error, bid must be higher than current Max Bid')
            return redirect(url_for('listing.show', id=id))
        if datetime.now > listing.closing_date:
            flash('Sorry, this auction has closed. No more bids will be accepted')
            return redirect(url_for('listing.show', id=id))
        listing.current_bid = bid.amount
        listing.total_bids = listing.total_bids + 1
        db.session.add(bid)
        db.session.commit()
        return redirect(url_for('listing.show', id=id))
    return render_template('listing/show.html', form=bid_instance)

#BUTTON TO ADD NOT ACTUAL SITE!!
@listing_bp.route('/<id>/add')
# @login_required
def add(id):

    watch_item = Watchlist()
    watch_item.user_id = current_user.get_id()
    watch_item.listing_id = id

    db.session.add(watch_item)
    db.session.commit()

    return redirect(url_for('listing.show', id=id))


# BUTTON TO REMOVE NOT ACTUAL PAGE!!
@listing_bp.route('/<id>/close')
# @login_required
def remove(id):
    user_query = Listing.query.filter_by(id=id).first
    if user_query.user_id == current_user.get_id():
        user_query.closing_date = datetime.now()
        user_query.state = "Closed"
        session.commit()
    else:
        flash("Sorry, only the original lister can close the bid")

    redirect((url_for('listing.show', id=id)))

from flask import Blueprint, render_template, request,session, redirect,url_for
from webapp.models.listing import Listing

mainbp = Blueprint('main', __name__)


@mainbp.route('/')
def index():
    listings = Listing.query.all()
    uniquelistings = Listing.query.with_entities(Listing.body_type).distinct()
    # to test db add , listings=listings to end of render tempate and uncooment above
    return render_template('index.html', listings=listings, uniquelistings = uniquelistings)

@mainbp.route('/search')
def search():
    this_request = request.values.get('search_term')

    if this_request:
        listings = Listing.query.filter_by(Listing.name.like(this_request)).all()

        return render_template('index.html', listings=listings)

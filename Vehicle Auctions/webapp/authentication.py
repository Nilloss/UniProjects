from flask import Blueprint, render_template, url_for, redirect, session, flash
from webapp.models.forms import LoginForm, RegisterForm
from webapp.models.user import User
from flask_login import login_user, login_required, logout_user
from webapp import db
from werkzeug.security import generate_password_hash, check_password_hash

auth_bp = Blueprint('authentication', __name__, url_prefix='/authentication')

@auth_bp.route('/login', methods=['GET', 'POST'])
def login():
    login_instance = LoginForm()

    if login_instance.validate_on_submit():
        user = User.query.filter_by(name=login_instance.user_name.data).first()

        if not user:
            flash('Username does not exist')
            return redirect((url_for('authentication.login')))

        if not check_password_hash(user.password_hash, login_instance.password.data):
            flash('Entered password is incorrect')
            return redirect((url_for('authentication.login')))

        login_user(user)
        return redirect(url_for('main.index'))

    return render_template("authentication/login.html", form=login_instance)


@auth_bp.route('/register', methods=['GET', 'POST'])
def register():
    register_form = RegisterForm()
    # if all forms are inputted correctly
    if register_form.validate_on_submit():
        existing_user = User.query.filter_by(name=register_form.user_name.data).first()

        # If username already exists in db.
        if existing_user:
            flash('Sorry this username already exists; please enter a unique username')
            return redirect(url_for('authentication.register'))

        user = User()
        user.name = register_form.user_name.data
        user.email = register_form.email.data
        user.phone_numb = register_form.phone_numb.data
        user.address = register_form.address.data

        user.password_hash = generate_password_hash(register_form.password.data)
        # create account and add to db.
        db.session.add(user)
        db.session.commit()
        return redirect(url_for('main.index'))
        
    return render_template("authentication/register.html", form=register_form)

@auth_bp.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('main.index'))

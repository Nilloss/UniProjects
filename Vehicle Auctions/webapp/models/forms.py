from flask_wtf import FlaskForm
from wtforms.fields import SubmitField, StringField, PasswordField, IntegerField, FloatField, TextAreaField
from wtforms.validators import InputRequired, Length, Email, EqualTo
from wtforms.fields.html5 import DateField
from flask_wtf.file import FileRequired, FileField, FileAllowed

# Create Form for auth/login
class LoginForm(FlaskForm):
    user_name = StringField('UserName', validators=[InputRequired('User Name is required')])
    password = PasswordField('Password', validators=[InputRequired('Password is required')])
    submit = SubmitField('Login')


# Create form for auth/register
class RegisterForm(FlaskForm):
    user_name = StringField('UserName', validators=[InputRequired('User Name is required')])

    email = StringField('Email', validators=[InputRequired('Email is Required'),
                                             Email('Valid email is required')])
    address = StringField('Address', validators=[InputRequired('Address is required')])
    phone_numb = IntegerField('Phone Number', validators=[InputRequired('Phone Number is Required')])
    password = PasswordField('Password', validators=[InputRequired('Password is Required'),
                                                     EqualTo('confirm_password',
                                                             message='Passwords do not match')])

    confirm_password = PasswordField('Confirm Password')
    submit = SubmitField('Create Account')


# Create form for listing/create
class ListingForm(FlaskForm):

    name = StringField('Name', validators=[InputRequired('Item Must have valid name')])
    startingBid = StringField('Starting Bid')
    make = StringField('Make')
    model = StringField('Model', validators=[InputRequired('A Model Type is required')])
    year = IntegerField('Year', validators=[InputRequired('A Year is required')])
    body_type = StringField('Body type')
    transmission = StringField('Transmission')
    fuel_type = StringField('Fuel Type')
    description = TextAreaField('Description', validators=[InputRequired('A Description is Required'), Length(25)])
    ALLOWED_FILE = {'png', 'jpg', 'JPG', 'PNG'}
    image_data = FileField('Destination Image', validators=[
        FileRequired(message='Image can not be empty'),
        FileAllowed(ALLOWED_FILE, message='Only supports png, jpg, JPG, PNG')])
    closing_date = DateField(validators=[InputRequired('A valid Date and Time is required ')])
    

    submit = SubmitField('Submit')


# Create form for listing/<id> to place bid
class BidForm(FlaskForm):
    amount = IntegerField('Bid', validators=[InputRequired('Valid bid price is required')])
    submit = SubmitField('Submit')
import React from 'react';
import UserService from '../../services/user.service';
import {User} from '../../models/user';
import './register.page.css';

export default class RegisterPage extends React.Component {

  constructor(props) {
    super(props);

    if(UserService.currentUserValue) {
      this.props.history.push('/');
    }

    this.state = {
      user: new User('','',''),
      submitted: false,
      loading: false,
      errorMessage: ''
    };
  }

  handleChange(e) {
    var { name, value } = e.target;
    var user = this.state.user;
    user[name] = value;
    this.setState({user: user});
  }

  handleRegister(e) {
    e.preventDefault();
    this.setState({submitted: true});
    const{user} = this.state;

    if(!(user.username && user.password && user.name)) {
      return;
    }

    this.setState({loading: true});
    UserService.register(user).then(data => {
      this.props.history.push("/login");
    }, error => {
      if(error.response.status === 409) {
        this.setState({
          errorMessage: "Username is not available",
          loading: false
        });
      }else {
        this.setState({
          errorMessage: "Unexpected error occurred.",
          loading: false
        });
      }
    });
  }

  render() {
    const {user, submitted, loading, errorMessage} = this.state;
    return (
      <div className="col-md-12">
        <div className="card card-container">
          <img id="profile-img" className="profile-img-card" src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"/>
          {errorMessage &&
            <div className="alert alert-danger" role="alert">
              <strong>Error! </strong> {errorMessage}
            </div>
          }
          <form name="form" onSubmit={(e) => this.handleRegister(e)}>
            <div className={'form-group' + (submitted && !user.name ? 'has-error':'')}>
              <label htmlFor="name">Full Name</label>
              <input type="text" className="form-control" name="name" value={user.name} onChange={(e)=>this.handleChange(e)}/>
              {submitted && !user.name &&
                <div className="help-block">Full name is required</div>
              }
            </div>
            <div className={'form-group' + (submitted && !user.username ? 'has-error':'')}>
              <label htmlFor="username">Username</label>
              <input type="text" className="form-control" name="username" value={user.username} onChange={(e)=>this.handleChange(e)}/>
              {submitted && !user.username &&
                <div className="help-block">Username is required</div>
              }
            </div>
            <div className={'form-group' + (submitted && !user.password ? 'has-error':'')}>
              <label htmlFor="password">Password</label>
              <input type="password" className="form-control" name="password" value={user.password} onChange={(e)=>this.handleChange(e)}/>
              {submitted && !user.password &&
                <div className="help-block">Password is required</div>
              }
            </div>
            <div className="form-group">
              <button className="btn btn-lg btn-primary btn-block btn-signin form-submit-button" disabled={loading}>Sign Up</button>
            </div>
          </form>
        </div>
      </div>
    );
  }

}

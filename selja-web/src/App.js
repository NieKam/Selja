import './App.css';
import { BrowserRouter as Router, Route, Switch, withRouter } from 'react-router-dom'
import AdItemDetails from './containers/AdItemsDetails'
import AdItemsList from './containers/AdItemsList'
import Footer from './components/Footer';
import Header from './components/Header'
import { Container } from '@material-ui/core'
import NewItemForm from './containers/NewItemForm'
import NotFound from './containers/NotFound/index.js'
import React, { Component } from 'react';

class App extends Component {
  state = {
    lat: null,
    long: null
  }

  componentDidMount = () => {
    navigator.geolocation.getCurrentPosition(
      position => {
        this.setState({
          lat: position.coords.latitude,
          long: position.coords.longitude
        })
      },
      err => console.log(err)
    )
  }

  render() {
    const { lat, long } = this.state
    return (
      <Router>
        <Header />
        <Container maxWidth="lg">
          <Switch>
            <Route exact path='/' component={() => <AdItemsList lat={lat} long={long} />} />
            <Route exact path='/items/:id' component={() => <AdItemDetails lat={lat} long={long} match={this.props.match} />} />
            <Route exact path='/newItem' component={() => <NewItemForm lat={lat} long={long} />} />
            <Route exact path='/notFound/:id' component={NotFound} />
            <Route component={NotFound} />
          </Switch>
        </Container>
        <Footer />
      </Router >
    )
  }
}

export default (App);

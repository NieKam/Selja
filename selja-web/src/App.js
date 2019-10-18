import './App.css';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import { makeStyles } from '@material-ui/core/styles'
import AdItemDetails from './containers/AdItemsDetails'
import AdItemsList from './containers/AdItemsList'
import Footer from './components/Footer';
import Header from './components/Header'
import { Container } from '@material-ui/core'
import NewItemForm from './containers/NewItemForm'
import NotFound from './containers/NotFound/index.js'
import React from 'react';

const useStyles = makeStyles(theme => ({
  root: {
    
  }
}))

function App() {
  const classes = useStyles()

  return (
    <Router>
        <Header />
        <Container maxWidth="lg">
          <Switch>
            <Route exact path='/' component={AdItemsList} />
            <Route exact path='/items/:id' component={AdItemDetails} />
            <Route exact path='/newItem' component={NewItemForm} />
            <Route exact path='/notFound/:id' component={NotFound} />
            <Route component={NotFound} />
          </Switch>
        </Container>
        <Footer />
    </Router>
  );
}

export default App;

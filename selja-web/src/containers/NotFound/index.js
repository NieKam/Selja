import { Redirect } from 'react-router-dom'
import { Typography, Paper } from '@material-ui/core';
import { withStyles } from "@material-ui/styles"
import PropTypes from "prop-types"
import React, { Component } from 'react';

const styles = theme => ({
    root: {
        padding: theme.spacing(10, 10)
    }
})
function ErrorTypography(props) {
    const { variant, text } = props
    return <Typography color="error" align="center" variant={variant}>{text}</Typography>
}

class NotFound extends Component {
    state = {
        seconds: 8,
    }

    componentDidMount = () => {
        const intervalId = setInterval(this.countdown, 1000)
        this.setState({ intervalId })
    }

    countdown = () => this.setState({ seconds: this.state.seconds - 1 })

    componentWillUnmount = () => clearInterval(this.state.intervalId)

    render() {
        const { location, match, classes } = this.props
        const { seconds } = this.state
        const locationText = match.params.id ? 'item with id ' + match.params.id : 'path ' + location.pathname

        return (
            <Paper className={classes.root}>
                <ErrorTypography variant="h6" text={`No match for ${locationText}`} />
                <ErrorTypography variant="subtitle1" text={`Back to home in ${seconds} seconds`} />
                {seconds === 0 && <Redirect to='/' />}
            </Paper>
        )
    }
}

NotFound.propTypes = {
    classes: PropTypes.object.isRequired
}

export default withStyles(styles)(NotFound)
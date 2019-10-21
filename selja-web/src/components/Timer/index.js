import { Chip } from "@material-ui/core";
import AccessTimeIcon from '@material-ui/icons/AccessTime';
import PropTypes from "prop-types"
import React from "react"

class Timer extends React.Component {

    formatTimeDifference = (futureTimestamp) => {
        let green = "#4caf50"
        let orange = "#ff9800"
        let red = "#f44336"

        let diff = new Date(futureTimestamp) - new Date()
        let days = Math.floor(diff / (1000 * 60 * 60 * 24))
        let hours = Math.floor(diff / (1000 * 60 * 60))
        let minutes = Math.floor(diff / (1000 * 60))

        let color;
        switch (true) {
            case days >= 2:
                color = green
                break;
            case days >= 1:
                color = orange
                break;
            default:
                color = red
        }

        if (days >= 1) {
            // display days and hours
            let nHours = hours - days * 24
            return {
                text: days + " days " + nHours + " hours",
                color: color
            }
        }

        if (hours >= 1) {
            // display hours and minutes
            let nMinutes = minutes - hours * 60
            return {
                text: hours + " hours " + nMinutes + " minutes",
                color: color
            }
        }

        // display minutes and seconds
        let seconds = diff / 1000 - minutes * 60
        return {
            text: minutes + " minutes " + seconds + " seconds",
            color: color
        }
    }

    render() {
        const { classes } = this.props
        const { text, color } = this.formatTimeDifference(this.props.validUntil)
        return (
            <Chip className={classes} variant="outlined" style={{ color }} label={text} icon={<AccessTimeIcon />} />
        )
    }
}

export default (Timer)
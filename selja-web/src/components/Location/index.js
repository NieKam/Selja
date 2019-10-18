import React from "react"
import { makeStyles } from "@material-ui/core/styles"
import { Typography } from "@material-ui/core";
import NearMe from '@material-ui/icons/NearMe';
import grey from "@material-ui/core/colors/grey"

const useStyles = makeStyles(theme => ({
    root: {
        display: "flex",
        alignItems: "center",
        marginRight: theme.spacing(2)
    },
    icon: {
        color: grey[500],
        marginRight: theme.spacing(1),
        width: 36,
        height: 36
    }
}))

export default function Location(props) {
    const classes = useStyles()

    return (
        <div className={classes.root}>
            <NearMe className={classes.icon} />
            <Typography variant="subtitle1" color="textPrimary" className={classes.text}>{props.distance.toFixed(2)} km</Typography>
        </div>
    )
}

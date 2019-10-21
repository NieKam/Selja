import { makeStyles } from "@material-ui/core/styles"
import { photoUrl } from "../../helpers/routes"
import Card from "@material-ui/core/Card"
import CardContent from "@material-ui/core/CardContent"
import CardMedia from "@material-ui/core/CardMedia"
import grey from "@material-ui/core/colors/grey"
import Location from "../Location"
import React from "react"
import Timer from "../Timer"
import Typography from "@material-ui/core/Typography"
import { CardActionArea } from '@material-ui/core';

const useStyles = makeStyles(theme => ({
    card: {
        display: "flex",
        border: 1,
        flexDirection: "row",
        borderColor: grey[200],
        borderStyle: "solid",
        marginBottom: theme.spacing(1)
    },
    details: {
        height: 180,
        display: "flex",
        flexDirection: "column",
        flexGrow: 1,
    },
    imageContainer: {
        width: 180,
        height: 180,
        display: "flex",
        alignItems: "center",
        justifyContent: "center"
    },
    image: {
        width: 150,
        height: 150,
        borderRadius: 8
    },
    mainText: {
        marginBottom: theme.spacing(2)
    },
    touchArea: {
        display: 'flex'
    },
    timer: {
        position: "absolute",
        bottom: theme.spacing(2)
    }
}))

export default function AdItem(props) {
    const classes = useStyles()
    const adItem = props.ad

    return (
        <Card className={classes.card} border={5} onClick={() => props.onClick(adItem.id)}>
            <CardActionArea className={classes.touchArea}>
                <div className={classes.imageContainer}>
                    <CardMedia
                        className={classes.image}
                        image={photoUrl(adItem.photoUrl)}
                        title="Ad image"
                    />
                </div>
                <div className={classes.details}>
                    <CardContent>
                        <Typography variant="h6" color="textSecondary" className={classes.mainText}>
                            {adItem.name}
                        </Typography>
                        <Typography variant="h5" color="textPrimary" className={classes.mainText}>
                            {adItem.price} {adItem.currency}
                        </Typography>
                        <Timer classes={classes.timer} validUntil={adItem.validUntilMs} />
                    </CardContent>
                </div>
                <Location distance={adItem.distanceInKm} />
            </CardActionArea>
        </Card>
    )
}
